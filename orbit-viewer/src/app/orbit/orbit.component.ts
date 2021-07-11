import {Component, OnInit} from '@angular/core';
import * as THREE from 'three';
import {Group, Mesh, Object3D, Scene, Vector3} from 'three';
import {OrbitdataService} from "../orbitdata.service";
import {ApiService, Planet} from "../api.service";
import {OrbitControls} from "three/examples/jsm/controls/OrbitControls";
import {MatSliderChange} from "@angular/material/slider";
import {Transfer} from "../transfer.js";
import {filter, map, toArray} from "rxjs/operators";
import {combineLatest, range} from "rxjs";

@Component({
  selector: 'app-orbit',
  templateUrl: './orbit.component.html',
  styleUrls: ['./orbit.component.css']
})
export class OrbitComponent implements OnInit {
  // TODO: Add AU to API
  private AU: number = 149597870700;
  // TODO: Add solar radius to API?
  private solarRadius: number = 800240666; // Solar radius in m. 1 Solar Radius = 1 axis unit.

  // TODO: Customizable ship performance.
  private maxDV = 71250; // 75% of 95 km / sec
  private maxFT = 147; // 75% of 28 week endurance

  // TODO: Customizable orbits
  private originOrbitHeight = 200000; // Default 200 km circular orbit.
  private targetOrbitHeight = 200000;

  // TODO: Figure out a better way to do colors.
  private planetColors: number[] = [
    0xFF0000,
    0xFFFF00,
    0x00FF00
  ]
  private pathColors: number[] = [
    0xFFB3B3,
    0xFFFFB3,
    0xB3FFB3
  ]

  // TODO: Selectable system
  private system_id: number = 1621827699;
  // TODO: Selectable star (or multiple stars within a system)
  private star_id: number = -1826843336;

  private frameScale: number = 7; // Default: 1 second = 7 days
  private planetScale: number = 1000; // Default: Planet locators are 1000x bigger than planet
  private zScale: number = 20; // Default: z Values are 20x larger
  private divisions: number = 60; // Default: Orbital paths are divided into 60 segments.

  elapsedTime: number = 0;
  private lastFrame !: number;

  private animating: boolean = false;
  working: boolean = true;

  scene: Scene = new THREE.Scene();
  private orbitsGroup: Group = new THREE.Group();
  private planetsGroup: Group = new THREE.Group();
  // private orbitsGroupName: string = "grp_orbits";
  private planetLocators: Mesh[] = [];

  private transfers: Transfer[] = [];

  constructor(private orbitDataService: OrbitdataService, private apiService: ApiService) {
  }

  ngOnInit(): void {
    this.scene.add(this.orbitsGroup);

    const canvas = <HTMLCanvasElement>document.getElementById('orbitplot');
    const renderer = new THREE.WebGLRenderer({canvas})

    const camera = new THREE.PerspectiveCamera();
    camera.position.z = 500;

    const controls = new OrbitControls(camera, renderer.domElement);
    controls.maxDistance = 500;
    controls.minDistance = 1;
    controls.rotateSpeed = 2;
    controls.minAzimuthAngle = 0;
    controls.maxAzimuthAngle = 0;
    controls.minPolarAngle = Math.PI / 2;
    controls.maxPolarAngle = Math.PI;
    controls.enablePan = false;

    {
      const light = new THREE.AmbientLight(0xffffff);
      this.scene.add(light);
    }

    {
      const geometry = new THREE.SphereGeometry(1);
      const material = new THREE.MeshBasicMaterial({color: 0xFFFFFF})
      const sphere = new THREE.Mesh(geometry, material);
      sphere.position.set(0, 0, 0);
      this.scene.add(sphere);

      const light = new THREE.PointLight();
      sphere.add(light);
    }

    combineLatest([
      this.apiService.getStar(this.system_id, this.star_id),
      this.apiService.getAllPlanets(this.system_id, this.star_id),
      this.apiService.getConstant("G"),
      this.apiService.getConstant("Msol"),
      this.apiService.getConstant("Mpln"),
      this.apiService.getConstant("Rpln")])
      .pipe(
        map(([star, planet, G, Msol, Mpln, Rpln]) => {
          planet.starGM = G.value * star.mass * Msol.value;
          planet.GM = G.value * planet.mass * Mpln.value;
          return {planet, Rpln};
        }))
      .subscribe(value => {
          const planet = value.planet;
          const planet_number = parseInt(planet.name[planet.name.length - 1]) - 1;

          {
            const planet_radius = planet.radius * value.Rpln.value;

            const planet_geometry = new THREE.SphereGeometry(planet_radius / this.solarRadius);
            const locator_geometry = new THREE.SphereGeometry(planet_radius / this.solarRadius * this.planetScale);

            const planet_material = new THREE.MeshStandardMaterial({color: this.planetColors[planet_number]});
            const locator_material = new THREE.MeshStandardMaterial({
              color: this.planetColors[planet_number],
              transparent: true,
              opacity: 0.50
            });

            const planet_sphere = new THREE.Mesh(planet_geometry, planet_material);
            const locator_sphere = new THREE.Mesh(locator_geometry, locator_material);

            planet_sphere.name = planet.name;
            planet_sphere.userData.planet = planet;
            planet_sphere.add(locator_sphere);
            this.updatePlanetPosition(planet_sphere, 0);

            this.planetLocators.push(locator_sphere);
            this.planetsGroup.add(planet_sphere);
            this.scene.add(this.planetsGroup);
          }

          {
            const period: number = this.orbitDataService.orbitalPeriod(planet);
            const times: number[] = Array(this.divisions + 1).fill(0).map((val, index) => index / this.divisions * period);
            const positions = times.map(time => {
              const [position] = this.orbitDataService.ephemeris(planet, time)
              return position;
            });
            positions.forEach(position => position.divideScalar(this.solarRadius));

            const geometry = new THREE.BufferGeometry().setFromPoints(positions);
            const material = new THREE.LineBasicMaterial({color: this.pathColors[planet_number]});
            const line = new THREE.Line(geometry, material);
            this.orbitsGroup.add(line);
          }
        },
        () => console.log("error"),
        () => {
          this.working = false;
          requestAnimationFrame(render);
        });

    const render: (time: number) => void = (time: number) => {
      if (this.lastFrame === undefined) this.lastFrame = time;

      if (this.animating) {
        const sinceLastFrame = ((time - this.lastFrame) / 1000) * 86400 * this.frameScale;
        this.elapsedTime += sinceLastFrame;

        this.planetsGroup.children.forEach(obj => this.updatePlanetPosition(obj, this.elapsedTime));
      }

      this.lastFrame = time;

      const pixelRatio = window.devicePixelRatio;
      const width = canvas.clientWidth * pixelRatio | 0;
      const height = canvas.clientHeight * pixelRatio | 0;

      if (canvas.width !== width || canvas.height !== height) {
        renderer.setSize(width, height, false);
        camera.aspect = canvas.clientWidth / canvas.clientHeight;
        camera.updateProjectionMatrix();
      }

      controls.update();
      renderer.render(this.scene, camera);
      requestAnimationFrame(render);
    };
  }

  updatePlanetPosition(object: Object3D, elapsedTime: number) {
    const planet: Planet = object.userData.planet;
    const [position] = this.orbitDataService.ephemeris(planet, elapsedTime);
    position.divideScalar(this.solarRadius);
    object.position.set(position.x, position.y, position.z);
  }

  drawTransfer(r1: Vector3, v1: Vector3, mu: number, tof: number) {
    const times: number[] = Array(this.divisions + 1).fill(0).map((val, index) => index / this.divisions * tof);
    const positions: Vector3[] = times.map(time => {
      const [position] = this.orbitDataService.propagate(r1, v1, mu, time);
      return position;
    });

    positions.forEach(position => {
      position.z *= this.zScale;
      position.divideScalar(this.solarRadius);
    });

    const geometry = new THREE.BufferGeometry().setFromPoints(positions);
    const material = new THREE.LineBasicMaterial({color: 0x0000FF});
    const line = new THREE.Line(geometry, material);
    this.scene.add(line);
  }










  // this.apiService.getPlanets(this.system_id, this.star_id)
    //   .subscribe(planets =>
    //     Promise.all(planets._embedded["trident-api:planet"].map((planet: EmbeddedPlanet) =>
    //       this.orbitDataService.getPath(this.system_id, this.star_id, planet.id)))
    //       .then((paths: Vector3[][]) => {
    //         const colors: number[] = [0xFFB3B3, 0xFFFFB3, 0xB3FFB3];
    //         const orbitsGroup: Group = new THREE.Group();
    //         orbitsGroup.name = this.orbitsGroupName;
    //
    //         paths.forEach((path, index) => {
    //           path.forEach(position => {
    //             position.z *= this.zScale;
    //             position.divideScalar(this.solarRadius);
    //           });
    //
    //           const geometry = new THREE.BufferGeometry().setFromPoints(path);
    //           const material = new THREE.LineBasicMaterial({color: colors[index]});
    //           const line = new THREE.Line(geometry, material);
    //           orbitsGroup.add(line);
    //         });
    //         this.scene.add(orbitsGroup);
    //       })
    //   );

    // Promise.all([
    //   this.orbitDataService.getRpln(),
    //   this.apiService.getPlanet(1621827699, -1826843336, 2035226060),
    //   this.apiService.getPlanet(1621827699, -1826843336, -154475081),
    //   this.apiService.getPlanet(1621827699, -1826843336, 159569841)
    // ])
    //   .then((promises: [Rpln: number, ...rest: Planet[]]) => {
    //     const colors: number[] = [0xFF0000, 0xFFFF00, 0x00FF00];
    //     const [Rpln, ...planets] = promises;
    //
    //     planets.forEach((planet, index) => {
    //       const planet_radius = planet.radius * Rpln;
    //
    //       const planet_geometry = new THREE.SphereGeometry(planet_radius / this.solarRadius, 24, 24);
    //       const locator_geometry = new THREE.SphereGeometry(planet_radius / this.solarRadius * this.planetScale, 24, 24);
    //
    //       const planet_material = new THREE.MeshStandardMaterial({
    //         color: colors[index]
    //       });
    //       const locator_material = new THREE.MeshStandardMaterial({
    //         color: colors[index],
    //         transparent: true,
    //         opacity: 0.50
    //       });
    //
    //       const planet_sphere = new THREE.Mesh(planet_geometry, planet_material);
    //       const locator_sphere = new THREE.Mesh(locator_geometry, locator_material);
    //
    //       planet_sphere.name = planet.name;
    //       planet_sphere.add(locator_sphere);
    //
    //       const [position]: [Vector3, Vector3] = this.orbitDataService.ephemeris(planet, 0);
    //       position.z *= this.zScale;
    //       position.multiplyScalar(this._AU / this.solarRadius);
    //       planet_sphere.position.set(position.x, position.y, position.z);
    //
    //       this.planetLocators.push(locator_sphere);
    //       this.scene.add(planet_sphere);
    //     });
    //
    //     return planets;
    //   })
    //   .then(planets => {
    //     const drawPlanet = (planet: Planet, elapsedTime: number) => {
    //       const [position]: [Vector3, Vector3] = this.orbitDataService.ephemeris(planet, elapsedTime);
    //       position.z *= this.zScale;
    //       position.multiplyScalar(this._AU / this.solarRadius);
    //       this.scene.getObjectByName(planet.name)?.position.set(position.x, position.y, position.z);
    //     }
    //
    //     const drawTransfer = (transfer: Transfer, elapsedTime: number) => {
    //       const transferTime: number = elapsedTime - transfer.startTime;
    //       const [position]: [Vector3, Vector3] = this.orbitDataService.propagate(transfer, transferTime);
    //       position.z *= this.zScale;
    //       position.divideScalar(this.solarRadius);
    //       this.scene.getObjectByName(transfer.name)?.position.set(position.x, position.y, position.z);
    //     }
    //
    //     const render = (time: number) => {
    //       if (lastFrame === undefined) lastFrame = time;
    //
    //       if (this.animating) {
    //         const sinceLastFrame = ((time - lastFrame) / 1000) * 86400 * this.frameScale;
    //         this.elapsedTime += sinceLastFrame;
    //       }
    //       planets.forEach(planet => drawPlanet(planet, this.elapsedTime));
    //       this.transfers.forEach((transfer, index) => {
    //         const transferSphere = this.scene.getObjectByName(transfer.name);
    //         if (transferSphere !== undefined) {
    //           const target = new Vector3(transfer.target.x, transfer.target.y, transfer.target.z);
    //           target.z *= this.zScale;
    //           target.divideScalar(this.solarRadius);
    //           console.log(transferSphere.position.distanceTo(target));
    //           if (transferSphere.position.distanceTo(target) < 0.5) {
    //             this.transfers.splice(index, 1);
    //             const transferPath = this.scene.getObjectByName(this.orbitsGroupName)?.getObjectByName(transfer.name + "-Path");
    //             if (transferPath !== undefined) this.scene.getObjectByName(this.orbitsGroupName)?.remove(transferPath);
    //             this.scene.remove(transferSphere);
    //           } else {
    //             drawTransfer(transfer, this.elapsedTime);
    //           }
    //         }
    //       });
    //
    //       lastFrame = time;
    //
    //       const pixelRatio = window.devicePixelRatio;
    //       const width = canvas.clientWidth * pixelRatio | 0;
    //       const height = canvas.clientHeight * pixelRatio | 0;
    //
    //       if (canvas.width !== width || canvas.height !== height) {
    //         renderer.setSize(width, height, false);
    //         camera.aspect = canvas.clientWidth / canvas.clientHeight;
    //         camera.updateProjectionMatrix();
    //       }
    //
    //       controls.update();
    //       renderer.render(this.scene, camera);
    //       requestAnimationFrame(render);
    //     }
    //     let lastFrame: number;
    //     this.working = false;
    //
    //     requestAnimationFrame(render);
    //   });


  handlePlayEvent(): void {
    this.animating = !this.animating;
  }

  handleSliderChange(event: MatSliderChange) {
    if (event.value != null) this.frameScale = event.value;
  }

  handleSeekEvent(forward: boolean) {
    const currentST = Math.floor(this.elapsedTime / 86400 / 14) + 1;
    if (forward) {
      this.elapsedTime = currentST * 14 * 86400;
    } else {
      const targetET = (currentST - 1) * 14 * 86400;
      if (this.elapsedTime - targetET < 200) {
        this.elapsedTime = (currentST - 2) * 14 * 86400;
      } else {
        this.elapsedTime = (currentST - 1) * 14 * 86400;
      }
      this.elapsedTime = Math.max(0, this.elapsedTime);
    }
    this.planetsGroup.children.forEach(obj => this.updatePlanetPosition(obj, this.elapsedTime));
  }

  handleOrbitsEvent(showOrbits: boolean) {
    this.orbitsGroup.visible = showOrbits;
  }

  handlePlanetLocatorsEvent(showPlanetLocators: boolean) {
    this.planetLocators.forEach(planetLocator => planetLocator.visible = showPlanetLocators);
  }

  getEpochDate(): string {
    const epochDate = new Date(2000, 0, 1);
    epochDate.setDate(epochDate.getDate() + Math.floor(this.elapsedTime / 86400));

    const returnDate = [
      epochDate.getFullYear().toString(),
      (epochDate.getMonth() + 1).toString().padStart(2, "0"),
      epochDate.getDate().toString().padStart(2, "0")
    ]

    return returnDate.join("-") + " 00:00:00";
  }

  handleLambertEvent(min_delta_v: boolean) {
    const origin: Planet = this.planetsGroup.getObjectByName("1 Omega Hydri 3")?.userData.planet;
    const target: Planet = this.planetsGroup.getObjectByName("1 Omega Hydri 1")?.userData.planet;

    const originOrbitRadius = 5954417.346258679;
    const targetOrbitRadius = 2366596.4289483577;

    const t1: number = 0;

    const transfers = range(1, this.maxFT)
      .pipe(
        map(tof => {
          tof *= 86400;
          const t2 = t1 + tof;

          const [r1, v1] = this.orbitDataService.ephemeris(origin, t1);
          const [r2, v2] = this.orbitDataService.ephemeris(target, t2);
          const [tv1, tv2] = this.orbitDataService.lambertSolver(r1, r2, tof, origin.starGM);


          const dv: number = this.orbitDataService.transferDeltaV(v1, tv1, origin.GM, originOrbitRadius)
            + this.orbitDataService.transferDeltaV(v2, tv2, target.GM, targetOrbitRadius);

          return {
            'flight_time': tof,
            'dv': dv,
            'r1': r1,
            'v1': tv1,
            'mu': origin.starGM
          };
        }),
        filter(result => result.dv <= this.maxDV && (result.flight_time / 86400) <= this.maxFT),
        toArray()
      );

    transfers.subscribe(transfers => {
      if (min_delta_v) {
        transfers.sort((e1, e2) => e1.dv - e2.dv);
      } else {
        transfers.sort((e1, e2) => e1.flight_time - e2.flight_time);
      }
      const selectedTransfer = transfers[0];

      this.drawTransfer(selectedTransfer.r1, selectedTransfer.v1, selectedTransfer.mu, selectedTransfer.flight_time);
    });




    // const tof = 30 * 86400;
    // const origin = this.planetsGroup.getObjectByName("1 Omega Hydri 3")?.userData.planet;
    // const target = this.planetsGroup.getObjectByName("1 Omega Hydri 1")?.userData.planet;
    //
    // const [r1] = this.orbitDataService.ephemeris(origin, 0);
    // const [r2] = this.orbitDataService.ephemeris(target, tof);
    //
    // const [v1] = this.orbitDataService.lambertSolver(r1, r2, tof, origin.starGM);
    //
    // {
    //   const times: number[] = Array(this.divisions + 1).fill(0).map((val, index) => index / this.divisions * tof);
    //   const positions: Vector3[] = times.map(time => {
    //     const [position] = this.orbitDataService.propagate(r1, v1, origin.starGM, time);
    //     return position;
    //   });
    //
    //   positions.forEach(position => {
    //     position.divideScalar(this.solarRadius);
    //     position.z *= this.zScale;
    //   })
    //
    //   const geometry = new THREE.BufferGeometry().setFromPoints(positions);
    //   const material = new THREE.LineBasicMaterial({color: 0x0000FF});
    //   const line = new THREE.Line(geometry, material);
    //   this.scene.add(line);
    // }




    // const color = min_delta_v ? 0xFF00FF : 0x00FFFF;
    // this.working = true;
    //
    // this.orbitDataService.getLambert(min_delta_v, this.getEpochDate(), 1621827699, -1826843336, 159569841, 2035226060)
    //   .then(([path, r1, r2, v1, mu]) => {
    //
    //     path.forEach(position => {
    //       position.z *= this.zScale;
    //       position.divideScalar(this.solarRadius);
    //     });
    //
    //     const transfer: Transfer = {
    //       mu: mu,
    //       name: min_delta_v ? "MinDV" : "MinFT",
    //       position: r1,
    //       target: r2,
    //       velocity: v1,
    //       startTime: this.elapsedTime
    //     }
    //     this.transfers.forEach((item, index) => {
    //       if (item.name === transfer.name) {
    //         this.transfers.splice(index, 1);
    //
    //         const transferPath = this.orbitsGroup.getObjectByName(transfer.name + "-Path");
    //         if (transferPath !== undefined) this.orbitsGroup.remove(transferPath);
    //
    //         const transferSphere = this.scene.getObjectByName(transfer.name);
    //         if (transferSphere !== undefined) this.scene.remove(transferSphere);
    //       }
    //     });
    //
    //     this.transfers.push(transfer);
    //     {
    //       const geometry = new THREE.BufferGeometry().setFromPoints(path);
    //       const material = new THREE.LineBasicMaterial({color: color});
    //       const line = new THREE.Line(geometry, material);
    //       line.name = transfer.name + "-Path";
    //       this.orbitsGroup.add(line);
    //     }
    //
    //     {
    //       const geometry = new THREE.SphereGeometry(2, 24, 24);
    //       const material = new THREE.MeshBasicMaterial({color: color});
    //       const mesh = new THREE.Mesh(geometry, material);
    //       const position = new Vector3(transfer.position.x, transfer.position.y, transfer.position.z);
    //       position.z *= this.zScale;
    //       position.divideScalar(this.solarRadius);
    //       mesh.position.set(position.x, position.y, position.z);
    //       mesh.name = transfer.name;
    //       this.scene.add(mesh);
    //     }
    //   })
    //   .finally(() => this.working = false);
  }
}
