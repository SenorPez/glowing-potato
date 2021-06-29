import {Component, OnInit} from '@angular/core';
import * as THREE from 'three';
import {OrbitdataService} from "../orbitdata.service";
import {OrbitControls} from "three/examples/jsm/controls/OrbitControls";

@Component({
  selector: 'app-orbit',
  templateUrl: './orbit.component.html',
  styleUrls: ['./orbit.component.css']
})
export class OrbitComponent implements OnInit {

  _AU: number = 149598000000;

  private solarRadius: number = 800240.666; // Solar radius in km. 1 Solar Radius = 1 axis unit.
  private zScale: number = 20; // z Scale

  constructor(private orbitDataService : OrbitdataService) { }

  ngOnInit(): void {
    const canvas = <HTMLCanvasElement>document.getElementById('orbitplot');
    const renderer = new THREE.WebGLRenderer({canvas})

    const camera = new THREE.PerspectiveCamera();
    camera.position.z = 400;

    const scene = new THREE.Scene();

    {
      const light = new THREE.AmbientLight(0xffffff);
      scene.add(light);
    }

    {
      const geometry = new THREE.SphereGeometry(1, 24, 24)
      const material = new THREE.MeshPhongMaterial({color: 0xFFFFFF})
      const sphere = new THREE.Mesh(geometry, material);
      sphere.position.set(0, 0, 0);
      scene.add(sphere);
    }

    const t0 = Date.now() / 1000;
    {
      this.orbitDataService.getPosition(1621827699, -1826843336, 2035226060, t0)
        .then(position => {
          position.z *= this.zScale;
          position.divideScalar(this.solarRadius * 1000);
          const geometry = new THREE.SphereGeometry(1, 24, 24)
          const material = new THREE.MeshPhongMaterial({color: 0xFF0000});
          const sphere = new THREE.Mesh(geometry, material);
          sphere.position.set(position.x, position.y, position.z)
          scene.add(sphere)
        })
    }

    {
      this.orbitDataService.getPosition(1621827699, -1826843336, -154475081, t0)
        .then(position => {
          position.divideScalar(this.solarRadius * 1000);
          position.z *= this.zScale;
          const geometry = new THREE.SphereGeometry(1, 24, 24)
          const material = new THREE.MeshPhongMaterial({color: 0xFFFF00});
          const sphere = new THREE.Mesh(geometry, material);
          sphere.position.set(position.x, position.y, position.z)
          scene.add(sphere)
        })
    }

    {
      this.orbitDataService.getPosition(1621827699, -1826843336, 159569841, t0)
        .then(position => {
          position.divideScalar(this.solarRadius * 1000);
          position.z *= this.zScale;
          const geometry = new THREE.SphereGeometry(1, 24, 24)
          const material = new THREE.MeshPhongMaterial({color: 0x00FF00});
          const sphere = new THREE.Mesh(geometry, material);
          sphere.position.set(position.x, position.y, position.z)
          scene.add(sphere)
        })
    }

    {
      this.orbitDataService.getEarthPosition(t0)
        .then(position => {
          position.divideScalar(this.solarRadius * 1000);
          position.z *= this.zScale;
          const geometry = new THREE.SphereGeometry(1, 24, 24)
          const material = new THREE.MeshPhongMaterial({color: 0x0000FF});
          const sphere = new THREE.Mesh(geometry, material);
          sphere.position.set(position.x, position.y, position.z)
          scene.add(sphere)
        })
    }

    {
      this.orbitDataService.getPath(1621827699, -1826843336, 2035226060, t0)
        .then(positions => {
          positions.forEach(position => {
            position.divideScalar(this.solarRadius * 1000);
            position.z *= this.zScale;
          })
          const geometry = new THREE.BufferGeometry().setFromPoints(positions)
          const material = new THREE.LineBasicMaterial({color: 0xFFB3B3});
          const line = new THREE.Line(geometry, material);
          scene.add(line);
        })
    }

    {
      this.orbitDataService.getPath(1621827699, -1826843336, -154475081, t0)
        .then(positions => {
          positions.forEach(position => {
            position.divideScalar(this.solarRadius * 1000);
            position.z *= this.zScale;
          })
          const geometry = new THREE.BufferGeometry().setFromPoints(positions)
          const material = new THREE.LineBasicMaterial({color: 0xFFFFB3});
          const line = new THREE.Line(geometry, material);
          scene.add(line);
        })
    }

    {
      this.orbitDataService.getPath(1621827699, -1826843336, 159569841, t0)
        .then(positions => {
          positions.forEach(position => {
            position.divideScalar(this.solarRadius * 1000);
            position.z *= this.zScale;
          })
          const geometry = new THREE.BufferGeometry().setFromPoints(positions)
          const material = new THREE.LineBasicMaterial({color: 0xB3FFB3});
          const line = new THREE.Line(geometry, material);
          scene.add(line);
        })
    }

    {
      this.orbitDataService.getEarthPath(t0)
        .then(positions => {
          positions.forEach(position => {
            position.divideScalar(this.solarRadius * 1000);
            position.z *= this.zScale;
          })
          const geometry = new THREE.BufferGeometry().setFromPoints(positions)
          const material = new THREE.LineBasicMaterial({color: 0xB3B3FF});
          const line = new THREE.Line(geometry, material);
          scene.add(line);
        })
    }

    const controls = new OrbitControls(camera, renderer.domElement);
    controls.maxDistance = 400;
    controls.minDistance = 1;
    controls.rotateSpeed = 2;
    controls.minAzimuthAngle = 0;
    controls.maxAzimuthAngle = 0;
    controls.minPolarAngle = Math.PI / 2;
    controls.maxPolarAngle = Math.PI;
    controls.enablePan = false;

    function render(time: number) {
      const pixelRatio = window.devicePixelRatio;
      const width = canvas.clientWidth * pixelRatio | 0;
      const height = canvas.clientHeight * pixelRatio | 0;
      if (canvas.width !== width || canvas.height !== height) {
        renderer.setSize(width, height, false);
        camera.aspect = canvas.clientWidth / canvas.clientHeight;
        camera.updateProjectionMatrix();
      }

      renderer.render(scene, camera);
      requestAnimationFrame(render);
      controls.update();
    }

    requestAnimationFrame(render);
  }
}

