import {Component, OnInit} from '@angular/core';
import * as THREE from 'three';
import {BufferGeometry, Group, Mesh, Object3D, Scene, Vector3} from 'three';
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

  // TODO: Customizable orbit parameters, including elliptical.
  private orbitRadius: number = 200000; // 200km circular parking orbit

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

  minDVTransferData: [number, number] | null = null;
  minFTTransferData: [number, number] | null = null;

  planets: Planet[] = [];
  points: {point: string, planet: Planet}[] = [];
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

    const camera = new THREE.OrthographicCamera(canvas.clientWidth / -2, canvas.clientWidth / 2, canvas.clientHeight / 2, canvas.clientHeight / -2, 0.01, 24000);
    camera.position.z = 12000;

    const controls = new OrbitControls(camera, renderer.domElement);
    controls.rotateSpeed = 2;
    controls.minAzimuthAngle = 0;
    controls.maxAzimuthAngle = 0;
    controls.minPolarAngle = Math.PI / 2;
    controls.maxPolarAngle = Math.PI;
    controls.enablePan = false;

    // Asteroid belt geometry
    // TODO: Add asteroid belts to API
    function fillWithPoints(geometry: BufferGeometry, groups: number, count: number) {
      const dummyTarget = new THREE.Vector3();
      const ray = new THREE.Ray();
      const boundGeometry = geometry.toNonIndexed();
      boundGeometry.computeBoundingBox();
      const boundingBox = boundGeometry.boundingBox;

      if (boundingBox === null) return;

      const points = new Array(groups);
      for (let i = 0; i < groups; i++) {
        points[i] = [];
      }

      const dir = new THREE.Vector3(1, 1, 1).normalize();
      let counter = 0;
      while (counter < count) {
        const point = new THREE.Vector3(
          THREE.MathUtils.randFloat(boundingBox.min.x, boundingBox.max.x),
          THREE.MathUtils.randFloat(boundingBox.min.y, boundingBox.max.y),
          THREE.MathUtils.randFloat(boundingBox.min.z, boundingBox.max.z)
        );
        if (isInside(point)) {
          point.z /= 2;
          const targetGroup: number = THREE.MathUtils.randInt(0, groups - 1);
          points[targetGroup].push(point);
          counter++;
        }
      }

      function isInside(point: Vector3) {
        ray.set(point, dir);
        let counter = 0;

        const pos = boundGeometry.getAttribute("position");
        const faces = pos.count / 3;
        const vA = new THREE.Vector3();
        const vB = new THREE.Vector3();
        const vC = new THREE.Vector3();
        for (let i = 0; i < faces; i++) {
          vA.fromBufferAttribute(pos, i * 3);
          vB.fromBufferAttribute(pos, i * 3 + 1);
          vC.fromBufferAttribute(pos, i * 3 + 2);
          if (ray.intersectTriangle(vA, vB, vC, false, dummyTarget)) counter++;
        }

        return counter % 2 == 1;
      }

      return points.map(arr => new THREE.BufferGeometry().setFromPoints(arr));
    }

    {
      const beltWidth = 0.05 * 1.496e11 / this.solarRadius;
      const beltPosition = 0.125 * 1.496e11 / this.solarRadius;
      const geometry = new THREE.TorusGeometry(beltPosition, beltWidth, 8, 100);
      const material = new THREE.MeshBasicMaterial({transparent: true, opacity: 0});
      const torus = new THREE.Mesh(geometry, material);
      this.scene.add(torus);

      const pointsGeometryArray = fillWithPoints(geometry, 3, 1500);
      const pointsColors = [0x594537, 0x817A75, 0x908D8C]
      pointsGeometryArray?.forEach((pointsGeometry, index) => {
          const pointsColor = pointsColors[index % pointsColors.length];
          const pointsMat = new THREE.PointsMaterial({color: pointsColor, size: 0.25, sizeAttenuation: false});
          const points = new THREE.Points(pointsGeometry, pointsMat);
          torus.add(points);
      });
    }

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

          // if (planet.name === "1 Omega Hydri 3") planet.eccentricity = 0;

          const lagrangeStability = this.orbitDataService.lagrangeStability(star.mass * Msol.value, planet.mass * Mpln.value);
          planet.lagrangePoints = {
            L1: false,
            L2: false,
            L3: false,
            L4: lagrangeStability,
            L5: lagrangeStability
          };

          return {planet, Rpln};
        }))
      .subscribe(value => {
          this.planets.push(value.planet);
          const planet = value.planet;
          const colorIndex = (parseInt(planet.name[planet.name.length - 1]) - 1) % this.planetColors.length;

          {
            const planetRadius = planet.radius * value.Rpln.value;

            const planetGeometry = new THREE.SphereGeometry(planetRadius / this.solarRadius);
            const locatorGeometry = new THREE.SphereGeometry(planetRadius / this.solarRadius * this.planetScale);
            const lagrange_geometry = new THREE.SphereGeometry(2);

            const planetMaterial = new THREE.MeshStandardMaterial({color: this.planetColors[colorIndex]});
            const locatorMaterial = new THREE.MeshStandardMaterial({
              color: this.planetColors[colorIndex],
              transparent: true,
              opacity: 0.50
            });
            const lagrange_material = new THREE.MeshBasicMaterial({color: 0xFFFFFF});

            const planetSphere = new THREE.Mesh(planetGeometry, planetMaterial);
            const locatorSphere = new THREE.Mesh(locatorGeometry, locatorMaterial);
            const L4Sphere = new THREE.Mesh(lagrange_geometry, lagrange_material);
            L4Sphere.name = "L4";
            const L5Sphere = new THREE.Mesh(lagrange_geometry, lagrange_material);
            L5Sphere.name = "L5";

            planetSphere.name = planet.name;
            planetSphere.userData.planet = planet;
            planetSphere.userData.planetRadius = planetRadius;
            planetSphere.add(locatorSphere);
            planetSphere.add(L4Sphere);
            planetSphere.add(L5Sphere);

            if (planet.name === "1 Omega Hydri 3") {
              L4Sphere.visible = true;
              L5Sphere.visible = true;
              this.points.push({point: "L4", planet: planet})
              this.points.push({point: "L5", planet: planet})
            } else {
              L4Sphere.visible = false;
              L5Sphere.visible = false;
            }
            this.updatePlanetPosition(planetSphere, 0);

            this.planetLocators.push(locatorSphere);
            this.planetsGroup.add(planetSphere);
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
            const material = new THREE.LineBasicMaterial({color: this.pathColors[colorIndex]});
            const line = new THREE.Line(geometry, material);
            this.orbitsGroup.add(line);
          }
        },
        (err) => console.log("error", err),
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
        // camera.aspect = canvas.clientWidth / canvas.clientHeight;
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
    object.updateMatrixWorld();

    const lagrangePoints = object.children.filter(obj =>
      (obj.name === "L4" || obj.name === "L5") && obj.visible);

    lagrangePoints.forEach(point => {
      const [position]: [Vector3, Vector3] = this.orbitDataService.lagrangePoint(planet, elapsedTime, point.name === "L4");
      position.z *= this.zScale;
      position.divideScalar(this.solarRadius);
      const localPosition = object.worldToLocal(position);
      point.position.set(localPosition.x, localPosition.y, localPosition.z);
      point.updateMatrixWorld();
    })
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
    const whichLagrange = (value: number | null) => value?.toString().substr(0, 1) === "L" ? value.toString().substr(0, 2) : null;
    const originLagrange = whichLagrange(this.origin);
    const targetLagrange = whichLagrange(this.target);

    const getPlanet = (value: number | null, lagrange: string | null) => {
      if (value !== null) {
        if (lagrange === null) {
          return this.planetsGroup.children.find(obj => obj.userData.planet.id === value)?.userData.planet;
        } else {
          const planetId = parseInt(value.toString().substr(2, value.toString().length));
          return this.planetsGroup.children.find(obj => obj.userData.planet.id === planetId)?.userData.planet;
        }
      }
    }
    const origin= getPlanet(this.origin, originLagrange);
    const target= getPlanet(this.target, targetLagrange);

    const getOrbitRadius = (value: number | null, lagrange: string | null) => {
      if (value !== null) {
        if (lagrange === null) {
          return this.planetsGroup.children.find(obj => obj.userData.planet.id === value)?.userData.planetRadius + this.orbitRadius;
        } else {
          return this.orbitRadius;
        }
      }
    }

    const originOrbitRadius = getOrbitRadius(this.origin, originLagrange);
    const targetOrbitRadius = getOrbitRadius(this.target, targetLagrange);

    const t1: number = this.elapsedTime;

    const transfers = range(1, this.maxFT)
      .pipe(
        map(tof => {
          tof *= 86400;
          const t2: number = t1 + tof;

          const [r1, v1]: Vector3[] = originLagrange === null
            ? this.orbitDataService.ephemerides(origin, t1)
            : this.orbitDataService.lagrangePoint(origin, t1, originLagrange === "L4");
          const [r2, v2]: Vector3[] = targetLagrange === null
            ? this.orbitDataService.ephemerides(target, t2)
            : this.orbitDataService.lagrangePoint(target, t2, targetLagrange === "L4");
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

        this.minDVTransferData = [selectedTransfer.dv, selectedTransfer.flight_time];

        this.minDVTransferObj.userData.transfer = {
          "r": selectedTransfer.r1,
          "v": selectedTransfer.v1,
          "mu": selectedTransfer.mu,
          "start_time": this.elapsedTime,
          "flight_time": selectedTransfer.flight_time,
          "dv": selectedTransfer.dv
        };
        this.minDVTransferObj.userData.path = this.minDVTransferPath;
        this.updateTransferPosition(this.minDVTransferObj, this.elapsedTime);
        this.transfersGroup.add(this.minDVTransferObj);
      } else {
        transfers.sort((a, b) => a.flight_time - b.flight_time);
        const selectedTransfer = transfers[0];
        this.minFTTransferPath.geometry = this.drawPath(selectedTransfer.r1, selectedTransfer.v1, selectedTransfer.mu, selectedTransfer.flight_time);
        this.scene.add(this.minFTTransferPath);

        this.minFTTransferData = [selectedTransfer.dv, selectedTransfer.flight_time];

        this.minFTTransferObj.userData.transfer = {
          "r": selectedTransfer.r1,
          "v": selectedTransfer.v1,
          "mu": selectedTransfer.mu,
          "start_time": this.elapsedTime,
          "flight_time": selectedTransfer.flight_time,
          "dv": selectedTransfer.dv
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
    this.origin = planets[0];
    this.target = planets[1];

    // Clear existing transfers on change.
    this.scene.remove(this.minFTTransferPath, this.minDVTransferPath);
    this.transfersGroup.remove(this.minFTTransferObj, this.minDVTransferObj);
    this.minFTTransferData = null;
    this.minDVTransferData = null;
  }
}
