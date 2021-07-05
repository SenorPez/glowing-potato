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

    {
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

          const render = (time: number) => {
            if (animationStart === undefined) animationStart = time;
            const elapsed = (time - animationStart) * 0.001 * 86400 * 7;

            planets.forEach(planet => {
              const [position]: [Vector3, Vector3] = this.orbitDataService.ephemeris(planet, elapsed);
              position.z *= this.zScale;
              position.multiplyScalar(this._AU / this.solarRadius);
              const sphere = this.scene.getObjectByName(planet.name);
              sphere?.position.set(position.x, position.y, position.z);
            })

            const pixelRatio = window.devicePixelRatio;
            const width = canvas.clientWidth * pixelRatio | 0;
            const height = canvas.clientHeight * pixelRatio | 0;
            if (canvas.width !== width || canvas.height !== height) {
              renderer.setSize(width, height, false);
              camera.aspect = canvas.clientWidth / canvas.clientHeight;
              camera.updateProjectionMatrix();
            }

            renderer.render(this.scene, camera);
            requestAnimationFrame(render);
            controls.update();
          }

          requestAnimationFrame(render);
        });
    }

    // {
    //   this.orbitDataService.getEarthPosition(t0)
    //     .then(position => {
    //       position.divideScalar(this.solarRadius * 1000);
    //       position.z *= this.zScale;
    //       const planet_radius = 6378.0
    //       const geometry = new THREE.SphereGeometry(planet_radius / this.solarRadius, 24, 24);
    //       const material = new THREE.MeshBasicMaterial({color: 0x0000FF});
    //       const sphere = new THREE.Mesh(geometry, material);
    //       sphere.position.set(position.x, position.y, position.z)
    //       this.scene.add(sphere)
    //
    //       const transparent_geometry = new THREE.SphereGeometry(planet_radius / this.solarRadius * this.planetScale, 24, 24);
    //       const transparent_material = new THREE.MeshStandardMaterial({
    //         color: 0x0000FF,
    //         transparent: true,
    //         opacity: 0.25
    //       });
    //       const transparent_sphere = new THREE.Mesh(transparent_geometry, transparent_material);
    //       transparent_sphere.position.set(position.x, position.y, position.z);
    //       this.scene.add(transparent_sphere);
    //     })
    // }
    //
    // {
    //   this.orbitDataService.getPath(1621827699, -1826843336, 2035226060, t0)
    //     .then(positions => {
    //       positions.forEach(position => {
    //         position.divideScalar(this.solarRadius * 1000);
    //         position.z *= this.zScale;
    //       })
    //       const geometry = new THREE.BufferGeometry().setFromPoints(positions)
    //       const material = new THREE.LineBasicMaterial({color: 0xFFB3B3});
    //       const line = new THREE.Line(geometry, material);
    //       this.scene.add(line);
    //     })
    // }
    //
    // {
    //   this.orbitDataService.getPath(1621827699, -1826843336, -154475081, t0)
    //     .then(positions => {
    //       positions.forEach(position => {
    //         position.divideScalar(this.solarRadius * 1000);
    //         position.z *= this.zScale;
    //       })
    //       const geometry = new THREE.BufferGeometry().setFromPoints(positions)
    //       const material = new THREE.LineBasicMaterial({color: 0xFFFFB3});
    //       const line = new THREE.Line(geometry, material);
    //       this.scene.add(line);
    //     })
    // }
    //
    // {
    //   this.orbitDataService.getPath(1621827699, -1826843336, 159569841, t0)
    //     .then(positions => {
    //       positions.forEach(position => {
    //         position.divideScalar(this.solarRadius * 1000);
    //         position.z *= this.zScale;
    //       })
    //       const geometry = new THREE.BufferGeometry().setFromPoints(positions)
    //       const material = new THREE.LineBasicMaterial({color: 0xB3FFB3});
    //       const line = new THREE.Line(geometry, material);
    //       this.scene.add(line);
    //     })
    // }
    //
    // {
    //   this.orbitDataService.getEarthPath(t0)
    //     .then(positions => {
    //       positions.forEach(position => {
    //         position.divideScalar(this.solarRadius * 1000);
    //         position.z *= this.zScale;
    //       })
    //       const geometry = new THREE.BufferGeometry().setFromPoints(positions)
    //       const material = new THREE.LineBasicMaterial({color: 0xB3B3FF});
    //       const line = new THREE.Line(geometry, material);
    //       this.scene.add(line);
    //     })
    // }
  }
}
