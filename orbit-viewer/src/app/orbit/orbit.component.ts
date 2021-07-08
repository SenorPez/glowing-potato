import {Component, OnInit} from '@angular/core';
import * as THREE from 'three';
import {Group, Mesh, Vector3} from 'three';
import {OrbitdataService} from "../orbitdata.service";
import {OrbitControls} from "three/examples/jsm/controls/OrbitControls";
import {Planet} from "../planet";
import {MatSliderChange} from "@angular/material/slider";
import {Transfer} from "../transfer.js";

@Component({
  selector: 'app-orbit',
  templateUrl: './orbit.component.html',
  styleUrls: ['./orbit.component.css']
})
export class OrbitComponent implements OnInit {
  // TODO: Add AU to API
  private _AU: number = 149598000000;
  // TODO: Add solar radius to API?
  private solarRadius: number = 800240666; // Solar radius in m. 1 Solar Radius = 1 axis unit.

  private frameScale: number = 7; // Default: 1 second = 7 days
  private planetScale: number = 1000; // Default: Planet locators are 1000x bigger than planet
  private zScale: number = 20; // Default: z Values are 20x larger

  scene !: THREE.Scene;

  elapsedTime: number = 0;
  private animating: boolean = false;
  working: boolean = true;

  private orbitsGroupName: string = "grp_orbits";
  private planetLocators: Mesh[] = [];

  private transfers: Transfer[] = [];

  constructor(private orbitDataService: OrbitdataService) {
    this.scene = new THREE.Scene();
  }

  ngOnInit(): void {
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
      const geometry = new THREE.SphereGeometry(1, 24, 24)
      const material = new THREE.MeshBasicMaterial({color: 0xFFFFFF})
      const sphere = new THREE.Mesh(geometry, material);
      sphere.position.set(0, 0, 0);
      this.scene.add(sphere);
    }

    Promise.all([
      this.orbitDataService.getPath(1621827699, -1826843336, 2035226060),
      this.orbitDataService.getPath(1621827699, -1826843336, -154475081),
      this.orbitDataService.getPath(1621827699, -1826843336, 159569841)
    ])
      .then((paths: Vector3[][]) => {
        const colors: number[] = [0xFFB3B3, 0xFFFFB3, 0xB3FFB3];
        const orbitsGroup: Group = new THREE.Group();
        orbitsGroup.name = this.orbitsGroupName;

        paths.forEach((path, index) => {
          path.forEach(position => {
            position.z *= this.zScale;
            position.divideScalar(this.solarRadius);
          });

          const geometry = new THREE.BufferGeometry().setFromPoints(path);
          const material = new THREE.LineBasicMaterial({color: colors[index]});
          const line = new THREE.Line(geometry, material);
          orbitsGroup.add(line);
        });
        this.scene.add(orbitsGroup);
      });

    Promise.all([
      this.orbitDataService.getRpln(),
      this.orbitDataService.getPlanet(1621827699, -1826843336, 2035226060),
      this.orbitDataService.getPlanet(1621827699, -1826843336, -154475081),
      this.orbitDataService.getPlanet(1621827699, -1826843336, 159569841)
    ])
      .then((promises: [Rpln: number, ...rest: Planet[]]) => {
        const colors: number[] = [0xFF0000, 0xFFFF00, 0x00FF00];
        const [Rpln, ...planets] = promises;

        planets.forEach((planet, index) => {
          const planet_radius = planet.radius * Rpln;

          const planet_geometry = new THREE.SphereGeometry(planet_radius / this.solarRadius, 24, 24);
          const locator_geometry = new THREE.SphereGeometry(planet_radius / this.solarRadius * this.planetScale, 24, 24);

          const planet_material = new THREE.MeshStandardMaterial({
            color: colors[index]
          });
          const locator_material = new THREE.MeshStandardMaterial({
            color: colors[index],
            transparent: true,
            opacity: 0.50
          });

          const planet_sphere = new THREE.Mesh(planet_geometry, planet_material);
          const locator_sphere = new THREE.Mesh(locator_geometry, locator_material);

          planet_sphere.name = planet.name;
          planet_sphere.add(locator_sphere);

          const [position]: [Vector3, Vector3] = this.orbitDataService.ephemeris(planet, 0);
          position.z *= this.zScale;
          position.multiplyScalar(this._AU / this.solarRadius);
          planet_sphere.position.set(position.x, position.y, position.z);

          this.planetLocators.push(locator_sphere);
          this.scene.add(planet_sphere);
        });

        return planets;
      })
      .then(planets => {
        const drawPlanet = (planet: Planet, elapsedTime: number) => {
          const [position]: [Vector3, Vector3] = this.orbitDataService.ephemeris(planet, elapsedTime);
          position.z *= this.zScale;
          position.multiplyScalar(this._AU / this.solarRadius);
          this.scene.getObjectByName(planet.name)?.position.set(position.x, position.y, position.z);
        }

        const drawTransfer = (transfer: Transfer, elapsedTime: number) => {
          const transferTime: number = elapsedTime - transfer.startTime;
          const [position]: [Vector3, Vector3] = this.orbitDataService.propagate(transfer, transferTime);
          position.z *= this.zScale;
          position.divideScalar(this.solarRadius);

          this.scene.getObjectByName(transfer.name)?.position.set(position.x, position.y, position.z);
        }

        const render = (time: number) => {
          if (lastFrame === undefined) lastFrame = time;

          if (this.animating) {
            const sinceLastFrame = ((time - lastFrame) / 1000) * 86400 * this.frameScale;
            this.elapsedTime += sinceLastFrame;
          }
          planets.forEach(planet => drawPlanet(planet, this.elapsedTime));
          this.transfers.forEach(transfer => drawTransfer(transfer, this.elapsedTime));

          lastFrame = time;

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
        }
        let lastFrame: number;
        this.working = false;

        requestAnimationFrame(render);
      });
  }

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
  }

  handleOrbitsEvent(showOrbits: boolean) {
    const orbitsGroup = this.scene.getObjectByName(this.orbitsGroupName);
    if (orbitsGroup !== undefined) orbitsGroup.visible = showOrbits;
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
    const color = min_delta_v ? 0xFF00FF : 0x00FFFF;
    this.working = true;

    this.orbitDataService.getLambert(min_delta_v, this.getEpochDate(), 1621827699, -1826843336, 159569841, 2035226060)
      .then(([path, r1, v1, mu]) => {

        path.forEach(position => {
          position.z *= this.zScale;
          position.divideScalar(this.solarRadius);
        });

        const transfer: Transfer = {
          mu: mu,
          name: min_delta_v ? "MinDV" : "MinFT",
          position: r1,
          velocity: v1,
          startTime: this.elapsedTime
        }
        this.transfers.forEach((item, index) => {
          if (item.name === transfer.name) {
            this.transfers.splice(index, 1);

            const transferPath = this.scene.getObjectByName(this.orbitsGroupName)?.getObjectByName(transfer.name + "-Path");
            if (transferPath !== undefined) this.scene.getObjectByName(this.orbitsGroupName)?.remove(transferPath);

            const transferSphere = this.scene.getObjectByName(transfer.name);
            if (transferSphere !== undefined) this.scene.remove(transferSphere);
          }
        });

        this.transfers.push(transfer);
        {
          const geometry = new THREE.BufferGeometry().setFromPoints(path);
          const material = new THREE.LineBasicMaterial({color: color});
          const line = new THREE.Line(geometry, material);
          line.name = transfer.name + "-Path";
          this.scene.getObjectByName(this.orbitsGroupName)?.add(line);
        }

        {
          const geometry = new THREE.SphereGeometry(2, 24, 24);
          const material = new THREE.MeshBasicMaterial({color: color});
          const mesh = new THREE.Mesh(geometry, material);
          const position = new Vector3(transfer.position.x, transfer.position.y, transfer.position.z);
          position.z *= this.zScale;
          position.divideScalar(this.solarRadius);
          mesh.position.set(position.x, position.y, position.z);
          mesh.name = transfer.name;
          this.scene.add(mesh);
        }
      })
      .finally(() => this.working = false);
  }
}
