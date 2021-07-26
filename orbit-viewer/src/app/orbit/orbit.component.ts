import {Component, OnInit} from '@angular/core';
import * as THREE from 'three';
import {Group, Mesh, Object3D, Scene, Vector3} from 'three';
import {OrbitdataService} from "../orbitdata.service";
import {ApiService, Planet} from "../api.service";
import {OrbitControls} from "three/examples/jsm/controls/OrbitControls";
import {MatSliderChange} from "@angular/material/slider";
import {filter, map, toArray} from "rxjs/operators";
import {combineLatest, range} from "rxjs";

@Component({
  selector: 'app-orbit',
  templateUrl: './orbit.component.html',
  styleUrls: ['./orbit.component.css']
})
export class OrbitComponent implements OnInit {
  // TODO: Add solar radius to API?
  private solarRadius: number = 800240666; // Solar radius in m. 1 Solar Radius = 1 axis unit.

  // TODO: Customizable ship performance.
  private maxDV = 71250; // 75% of 95 km / sec
  private maxFT = 147; // 75% of 28 week endurance

  // TODO: Figure out a way to tie colors to specific planets.
  private planetColors: number[] = [
    0xFF0000,
    0xFFFF00,
    0x00FF00,
    0x00FFFF,
    0x0000FF,
    0xFF00FF
  ]
  private pathColors: number[] = [
    0xFFB3B3,
    0xFFFFB3,
    0xB3FFB3,
    0xB3FFFF,
    0xB3B3FF,
    0xFFB3FF
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
  private showTransfers: boolean = true;

  scene: Scene = new THREE.Scene();
  private orbitsGroup: Group = new THREE.Group();
  private planetsGroup: Group = new THREE.Group();
  private transfersGroup: Group = new THREE.Group();
  private planetLocators: Mesh[] = [];

  private colorDV = {color: 0x0000FF};
  private colorFT = {color: 0xFF00FF};

  private minDVTransferPath = new THREE.Line(new THREE.BufferGeometry(), new THREE.LineBasicMaterial(this.colorDV));
  private minFTTransferPath = new THREE.Line(new THREE.BufferGeometry(), new THREE.LineBasicMaterial(this.colorFT));
  private minDVTransferObj = new THREE.Mesh(
    new THREE.SphereGeometry(3),
    new THREE.MeshBasicMaterial(this.colorDV)
  );
  private minFTTransferObj = new THREE.Mesh(
    new THREE.SphereGeometry(3),
    new THREE.MeshBasicMaterial(this.colorFT)
  );

  planets: Planet[] = [];
  private origin: number | null = null;
  private target: number | null = null;

  constructor(private orbitDataService: OrbitdataService, private apiService: ApiService) {
  }

  ngOnInit(): void {
    this.scene.add(this.orbitsGroup);
    this.scene.add(this.planetsGroup);
    this.scene.add(this.transfersGroup);

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

      const light = new THREE.PointLight();
      sphere.add(light);

      this.scene.add(sphere);
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
          this.planets.push(value.planet);
          const planet = value.planet;
          const color_index = (parseInt(planet.name[planet.name.length - 1]) - 1) % this.planetColors.length;

          {
            const planet_radius = planet.radius * value.Rpln.value;

            const planet_geometry = new THREE.SphereGeometry(planet_radius / this.solarRadius);
            const locator_geometry = new THREE.SphereGeometry(planet_radius / this.solarRadius * this.planetScale);

            const planet_material = new THREE.MeshStandardMaterial({color: this.planetColors[color_index]});
            const locator_material = new THREE.MeshStandardMaterial({
              color: this.planetColors[color_index],
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
          }

          {
            const period: number = this.orbitDataService.orbitalPeriod(planet);
            const times: number[] = Array(this.divisions + 1).fill(0).map((val, index) => index / this.divisions * period);
            const positions = times.map(time => {
              const [position] = this.orbitDataService.ephemerides(planet, time)
              return position;
            });
            positions.forEach(position => {
              position.z *= this.zScale;
              position.divideScalar(this.solarRadius);
            });

            const geometry = new THREE.BufferGeometry().setFromPoints(positions);
            const material = new THREE.LineBasicMaterial({color: this.pathColors[color_index]});
            const line = new THREE.Line(geometry, material);
            this.orbitsGroup.add(line);
          }
        },
        () => console.log("error"),
        () => {
          this.planets.sort((a, b) => parseInt(a.name[a.name.length - 1]) - parseInt(b.name[b.name.length - 1]));
          this.working = false;
          requestAnimationFrame(render);
        });

    const render: (time: number) => void = (time: number) => {
      if (this.lastFrame === undefined) this.lastFrame = time;

      if (this.animating) {
        const sinceLastFrame = ((time - this.lastFrame) / 1000) * 86400 * this.frameScale;
        this.elapsedTime += sinceLastFrame;

        this.planetsGroup.children.forEach(obj => this.updatePlanetPosition(obj, this.elapsedTime));
        this.transfersGroup.children.forEach(obj => this.updateTransferPosition(obj, this.elapsedTime));
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

  drawPath(r1: Vector3, v1: Vector3, mu: number, tof: number) {
    const times: number[] = Array(this.divisions).fill(0).map((val, index) => index / (this.divisions - 1) * tof);
    const positions: Vector3[] = times.map(time => {
      const [position] = this.orbitDataService.propagate(r1, v1, mu, time);
      return position;
    });

    positions.forEach(position => {
      position.z *= this.zScale;
      position.divideScalar(this.solarRadius);
    });

    return new THREE.BufferGeometry().setFromPoints(positions);
  }

  updatePlanetPosition(object: Object3D, elapsedTime: number) {
    const planet: Planet = object.userData.planet;
    const [position] = this.orbitDataService.ephemerides(planet, elapsedTime);
    position.z *= this.zScale;
    position.divideScalar(this.solarRadius);
    object.position.set(position.x, position.y, position.z);
  }

  updateTransferPosition(object: Object3D, elapsedTime: number) {
    const transfer = object.userData.transfer;

    if (elapsedTime - transfer.start_time > transfer.flight_time || elapsedTime - transfer.start_time < 0) {
      object.visible = false;
      object.userData.path.visible = false;
    } else {
      object.visible = this.showTransfers;
      object.userData.path.visible = this.showTransfers;
      const [position] = this.orbitDataService.propagate(transfer.r, transfer.v, transfer.mu, elapsedTime - transfer.start_time);
      position.z *= this.zScale;
      position.divideScalar(this.solarRadius);
      object.position.set(position.x, position.y, position.z);
    }
  }

  handleOrbitsEvent(showOrbits: boolean) {
    this.orbitsGroup.visible = showOrbits;
  }

  handlePlanetLocatorsEvent(showPlanetLocators: boolean) {
    this.planetLocators.forEach(planetLocator => planetLocator.visible = showPlanetLocators);
  }

  handlePlayEvent(): void {
    this.animating = !this.animating;
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
    this.transfersGroup.children.forEach(obj => this.updateTransferPosition(obj, this.elapsedTime));
  }

  handleSliderEvent(event: MatSliderChange) {
    if (event.value != null) this.frameScale = event.value;
  }

  handleTransferEvent(min_delta_v: boolean) {
    const origin: Planet = this.planetsGroup.children.find(obj => obj.userData.planet.id === this.origin)?.userData.planet;
    const target: Planet = this.planetsGroup.children.find(obj => obj.userData.planet.id === this.target)?.userData.planet;

    const originOrbitRadius: number = 5954417.346258679;
    const targetOrbitRadius: number = 2366596.4289483577;

    const t1: number = this.elapsedTime;

    const transfers = range(1, this.maxFT)
      .pipe(
        map(tof => {
          tof *= 86400;
          const t2: number = t1 + tof;

          const [r1, v1]: Vector3[] = this.orbitDataService.ephemerides(origin, t1);
          const [r2, v2]: Vector3[] = this.orbitDataService.ephemerides(target, t2);
          const [tv1, tv2]: Vector3[] = this.orbitDataService.transfer(r1, r2, tof, origin.starGM);

          const dv: number = this.orbitDataService.transferDeltaV(v1, tv1, origin.GM, originOrbitRadius)
            + this.orbitDataService.transferDeltaV(v2, tv2, target.GM, targetOrbitRadius);

          return {
            'flight_time': tof,
            'dv': dv,
            'r1': r1,
            'pv1': v1,
            'r2': r2,
            'pv2': v2,
            'v1': tv1,
            'v2': tv2,
            'mu': origin.starGM
          };
        }),
        filter(result => result.dv <= this.maxDV && (result.flight_time / 86400) <= this.maxFT),
        toArray()
      );

    transfers.subscribe(transfers => {
      if (min_delta_v) {
        transfers.sort((a, b) => a.dv - b.dv);
        const selectedTransfer = transfers[0];
        this.minDVTransferPath.geometry = this.drawPath(selectedTransfer.r1, selectedTransfer.v1, selectedTransfer.mu, selectedTransfer.flight_time);
        this.scene.add(this.minDVTransferPath);

        this.minDVTransferObj.userData.transfer = {
          "r": selectedTransfer.r1,
          "v": selectedTransfer.v1,
          "mu": selectedTransfer.mu,
          "start_time": this.elapsedTime,
          "flight_time": selectedTransfer.flight_time
        };
        this.minDVTransferObj.userData.path = this.minDVTransferPath;
        this.updateTransferPosition(this.minDVTransferObj, this.elapsedTime);
        this.transfersGroup.add(this.minDVTransferObj);
      } else {
        transfers.sort((a, b) => a.flight_time - b.flight_time);
        const selectedTransfer = transfers[0];
        this.minFTTransferPath.geometry = this.drawPath(selectedTransfer.r1, selectedTransfer.v1, selectedTransfer.mu, selectedTransfer.flight_time);
        this.scene.add(this.minFTTransferPath);

        this.minFTTransferObj.userData.transfer = {
          "r": selectedTransfer.r1,
          "v": selectedTransfer.v1,
          "mu": selectedTransfer.mu,
          "start_time": this.elapsedTime,
          "flight_time": selectedTransfer.flight_time
        };
        this.minFTTransferObj.userData.path = this.minFTTransferPath;
        this.updateTransferPosition(this.minFTTransferObj, this.elapsedTime);
        this.transfersGroup.add(this.minFTTransferObj);
      }
    });
  }

  handleTransfersEvent(showTransfers: boolean) {
    this.showTransfers = showTransfers;
    if (!this.animating) {
      this.transfersGroup.children.forEach(obj => this.updateTransferPosition(obj, this.elapsedTime));
    }
  }

  handlePlanetsChangeEvent(planets: (number | null)[]) {
    console.log(planets);
    this.origin = planets[0];
    this.target = planets[1];
  }
}
