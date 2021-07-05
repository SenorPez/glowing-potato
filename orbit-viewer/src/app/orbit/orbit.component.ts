import {Component, OnInit} from '@angular/core';
import * as THREE from 'three';
import {Vector3} from 'three';
import {OrbitdataService} from "../orbitdata.service";
import {OrbitControls} from "three/examples/jsm/controls/OrbitControls";
import {Planet} from "../planet";

@Component({
  selector: 'app-orbit',
  templateUrl: './orbit.component.html',
  styleUrls: ['./orbit.component.css']
})
export class OrbitComponent implements OnInit {

  // TODO: Add AU to API
  _AU: number = 149598000000;

  // TODO: Add solar radius to API?
  private solarRadius: number = 800240666; // Solar radius in m. 1 Solar Radius = 1 axis unit.

  private zScale: number = 20; // z Scale
  private planetScale: number = 1000;
  scene !: THREE.Scene;

  private animating: boolean = false;

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
        paths.forEach((path, index) => {
          path.forEach(position => {
            position.z *= this.zScale;
            position.divideScalar(this.solarRadius);
          });

          const geometry = new THREE.BufferGeometry().setFromPoints(path);
          const material = new THREE.LineBasicMaterial({color: colors[index]});
          const line = new THREE.Line(geometry, material);
          this.scene.add(line);
        });
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
          const geometry = new THREE.SphereGeometry(planet_radius / this.solarRadius * this.planetScale, 24, 24);
          const material = new THREE.MeshStandardMaterial({
            color: colors[index],
            transparent: true,
            opacity: .99
          });
          const sphere = new THREE.Mesh(geometry, material);
          const [position]: [Vector3, Vector3] = this.orbitDataService.ephemeris(planet, 0);
          position.z *= this.zScale;
          position.multiplyScalar(this._AU / this.solarRadius);
          sphere.position.set(position.x, position.y, position.z);
          sphere.name = planet.name;
          this.scene.add(sphere);
        })

        return planets;
      })
      .then(planets => {
        let animationStart: number;
        let elapsed: number = 0;
        let lastStop: number = 0;

        const render = (time: number) => {
          if (this.animating || animationStart === undefined) {
            if (animationStart === undefined) animationStart = time;
            elapsed = (time - (animationStart + lastStop));
            const frameTime = (elapsed / 1000) * 60 * 60 * 24 * 7;

            planets.forEach(planet => {
              const [position]: [Vector3, Vector3] = this.orbitDataService.ephemeris(planet, frameTime);
              position.z *= this.zScale;
              position.multiplyScalar(this._AU / this.solarRadius);
              const sphere = this.scene.getObjectByName(planet.name);
              sphere?.position.set(position.x, position.y, position.z);
            })
          } else {
            lastStop = (time - animationStart - elapsed);
          }

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

        requestAnimationFrame(render);
      });
  }

  handlePlayEvent(): void {
    this.animating = !this.animating;
  }
}
