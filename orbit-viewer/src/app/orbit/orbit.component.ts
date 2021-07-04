import {Component, OnInit} from '@angular/core';
import * as THREE from 'three';
import {Vector3} from 'three';
import {OrbitdataService} from "../orbitdata.service";
import {OrbitControls} from "three/examples/jsm/controls/OrbitControls";

@Component({
  selector: 'app-orbit',
  templateUrl: './orbit.component.html',
  styleUrls: ['./orbit.component.css']
})
export class OrbitComponent implements OnInit {

  _AU: number = 149598000000;

  private solarRadius: number = 800240666; // Solar radius in m. 1 Solar Radius = 1 axis unit.
  // TODO: Add solar radius to API?
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
        this.orbitDataService.getPlanet(1621827699, -1826843336, 2035226060),
        this.orbitDataService.getRpln()
      ])
        .then(promises => {
          const planet = promises[0];
          const Rpln = promises[1];

          const planet_radius = planet.radius * Rpln;
          const geometry = new THREE.SphereGeometry(planet_radius / this.solarRadius * this.planetScale, 24, 24);
          const material = new THREE.MeshStandardMaterial({
            color: 0xFF0000,
            transparent: true,
            opacity: .99
          });

          const sphere = new THREE.Mesh(geometry, material);
          const eph: [Vector3, Vector3] = this.orbitDataService.ephemeris(planet, 0);
          const position: Vector3 = eph[0];
          position.multiplyScalar(this._AU / this.solarRadius);
          sphere.position.set(position.x, position.y, position.z);
          sphere.name = "1 Omega Hydri 3";
          this.scene.add(sphere);

          return planet;
        })
        .then(planet => {
          let animationStart: number;

          const render = (time: number) => {
            if (animationStart === undefined) animationStart = time;
            const elapsed = (time - animationStart) * 0.001 * 86400 * 7;

            const [position, velocity]: [Vector3, Vector3] = this.orbitDataService.ephemeris(planet, elapsed);
            position.multiplyScalar(this._AU / this.solarRadius);
            const sphere = this.scene.getObjectByName("1 Omega Hydri 3");
            sphere?.position.set(position.x, position.y, position.z);

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
        })

    //       const planet_radius = planet.radius * Rpln;
    //       const geometry = new THREE.SphereGeometry(planet_radius, this.solarRadius * this.planetScale, 24, 24);
    //       const material = new THREE.MeshStandardMaterial({
    //         color: 0xFF0000,
    //         transparent: true,
    //         opacity: 0.25
    //       });
    //
    //       const sphere = new THREE.Mesh(geometry, material);
    //       const eph: [Vector3, Vector3] = this.orbitDataService.ephemeris(planet, 0);
    //       const position: Vector3 = eph[0];
    //       position.multiplyScalar(this._AU / (this.solarRadius * 1000));
    //       sphere.position.set(position.x, position.y, position.z);
    //       this.scene.add(sphere);
    //     })
    }

    // {
    //   const planet_radius = 2164.0
    //   const geometry = new THREE.SphereGeometry(planet_radius / this.solarRadius * this.planetScale, 24, 24);
    //   const material = new THREE.MeshStandardMaterial({
    //     color: 0xFF0000,
    //     transparent: true,
    //     opacity: 0.25
    //   });
    //   const sphere = new THREE.Mesh(geometry, material);
    //   const ephemeris = this.orbitDataService.ephemeris(testPlanet, 0);
    //   let position = new Vector3(ephemeris[0][0], ephemeris[0][1], ephemeris[0][2]);
    //
    //   position.multiplyScalar(this._AU / (this.solarRadius * 1000));
    //   sphere.position.set(position.x, position.y, position.z);
    //   this.scene.add(sphere);
    // }




    // const t0 = Date.now() / 1000;
    //
    // // TODO: Pull planet radius from API.
    // Promise.all([
    //     this.orbitDataService.getPosition(1621827699, -1826843336, 2035226060, 0)
    //       .then(position => {
    //         const planet_radius = 2164.0
    //         const transparent_geometry = new THREE.SphereGeometry(planet_radius / this.solarRadius * this.planetScale, 24, 24);
    //         const transparent_material = new THREE.MeshStandardMaterial({
    //           color: 0xFF0000,
    //           transparent: true,
    //           opacity: 0.25
    //         });
    //         const transparent_sphere = new THREE.Mesh(transparent_geometry, transparent_material);
    //         transparent_sphere.name = "1 Omega Hydri 1";
    //
    //         position.z *= this.zScale;
    //         position.divideScalar(this.solarRadius * 1000);
    //         transparent_sphere.position.set(position.x, position.y, position.z);
    //         this.scene.add(transparent_sphere)
    //       }),
    //     this.orbitDataService.getPosition(1621827699, -1826843336, -154475081, 0)
    //       .then(position => {
    //         const planet_radius = 4590.0
    //         const transparent_geometry = new THREE.SphereGeometry(planet_radius / this.solarRadius * this.planetScale, 24, 24);
    //         const transparent_material = new THREE.MeshStandardMaterial({
    //           color: 0xFFFF00,
    //           transparent: true,
    //           opacity: 0.25
    //         });
    //         const transparent_sphere = new THREE.Mesh(transparent_geometry, transparent_material);
    //         transparent_sphere.name = "1 Omega Hydri 2";
    //
    //         position.z *= this.zScale;
    //         position.divideScalar(this.solarRadius * 1000);
    //         transparent_sphere.position.set(position.x, position.y, position.z);
    //         this.scene.add(transparent_sphere)
    //       }),
    //     this.orbitDataService.getPosition(1621827699, -1826843336, 159569841, 0)
    //       .then(position => {
    //         const planet_radius = 5747.0
    //         const transparent_geometry = new THREE.SphereGeometry(planet_radius / this.solarRadius * this.planetScale, 24, 24);
    //         const transparent_material = new THREE.MeshStandardMaterial({
    //           color: 0x00FF00,
    //           transparent: true,
    //           opacity: 0.25
    //         });
    //         const transparent_sphere = new THREE.Mesh(transparent_geometry, transparent_material);
    //         transparent_sphere.name = "1 Omega Hydri 3";
    //
    //         position.z *= this.zScale;
    //         position.divideScalar(this.solarRadius * 1000);
    //         transparent_sphere.position.set(position.x, position.y, position.z);
    //         this.scene.add(transparent_sphere)
    //       })
    //   ]
    // ).then(() => requestAnimationFrame(render));

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

    // const render = (time: number) => {
    //   time *= 0.001 * 604800;
    //
    //   console.log(time);
    //   const pixelRatio = window.devicePixelRatio;
    //   const width = canvas.clientWidth * pixelRatio | 0;
    //   const height = canvas.clientHeight * pixelRatio | 0;
    //   if (canvas.width !== width || canvas.height !== height) {
    //     renderer.setSize(width, height, false);
    //     camera.aspect = canvas.clientWidth / canvas.clientHeight;
    //     camera.updateProjectionMatrix();
    //   }
    //
    //   if (this.orbitDataService.inCache(2035226060)) {
    //     this.orbitDataService.getCachedPlanetPosition(2035226060, time)
    //       .then(position => {
    //         position.z *= this.zScale;
    //         position.divideScalar(this.solarRadius * 1000);
    //         const object = this.scene.getObjectByName("1 Omega Hydri 1");
    //         object?.position.set(position.x, position.y, position.z);
    //       });
    //   }
    //
    //   if (this.orbitDataService.inCache(-154475081)) {
    //     this.orbitDataService.getCachedPlanetPosition(-154475081, time)
    //       .then(position => {
    //         position.z *= this.zScale;
    //         position.divideScalar(this.solarRadius * 1000);
    //         const object = this.scene.getObjectByName("1 Omega Hydri 2");
    //         object?.position.set(position.x, position.y, position.z);
    //       });
    //   }
    //
    //   if (this.orbitDataService.inCache(159569841)) {
    //     this.orbitDataService.getCachedPlanetPosition(159569841, time)
    //       .then(position => {
    //         position.z *= this.zScale;
    //         position.divideScalar(this.solarRadius * 1000);
    //         const object = this.scene.getObjectByName("1 Omega Hydri 3");
    //         object?.position.set(position.x, position.y, position.z);
    //       });
    //   }
    //
    //   renderer.render(this.scene, camera);
    //   requestAnimationFrame(render);
    //   controls.update();
    // }
  }
}

// function updatePosition(renderer: THREE.Renderer, scene: THREE.Scene, camera: THREE.Camera, geometry: THREE.BufferGeometry, material: THREE.Material, group: THREE.Group) {
//   // @ts-ignore
//   this.orbitDataService.getCachedPlanetPosition(2035226060)
// }

//
//
// this.orbitDataService.getPosition(1621827699, -1826843336, 2035226060, 0)
//   .then(position => {
//     position.z *= this.zScale;
//     position.divideScalar(this.solarRadius * 1000);
//     const planet_radius = 2164.0
//     const geometry = new THREE.SphereGeometry(planet_radius / this.solarRadius, 24, 24);
//     const material = new THREE.MeshBasicMaterial({color: 0xFF0000});
//     const sphere = new THREE.Mesh(geometry, material);
//     sphere.position.set(position.x, position.y, position.z);
//     this.scene.add(sphere);
//
//     const transparent_geometry = new THREE.SphereGeometry(planet_radius / this.solarRadius * this.planetScale, 24, 24);
//     const transparent_material = new THREE.MeshStandardMaterial({
//       color: 0xFF0000,
//       transparent: true,
//       opacity: 0.25
//     });
//     const transparent_sphere = new THREE.Mesh(transparent_geometry, transparent_material);
//     transparent_sphere.onBeforeRender = updatePosition;
//     transparent_sphere.position.set(position.x, position.y, position.z);
//     this.scene.add(transparent_sphere);
//   })
