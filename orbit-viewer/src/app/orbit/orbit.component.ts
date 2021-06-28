import {Component, OnInit} from '@angular/core';
import * as THREE from 'three';
import {OrbitdataService} from "../orbitdata.service";
import {TrackballControls} from "three/examples/jsm/controls/TrackballControls";

@Component({
  selector: 'app-orbit',
  templateUrl: './orbit.component.html',
  styleUrls: ['./orbit.component.css']
})
export class OrbitComponent implements OnInit {

  _AU: number = 149598000000;

  constructor(private orbitDataService : OrbitdataService) { }

  ngOnInit(): void {
    const canvas = <HTMLCanvasElement>document.getElementById('orbitplot');
    const renderer = new THREE.WebGLRenderer({canvas})

    const camera = new THREE.PerspectiveCamera();
    camera.position.z = 10;

    const scene = new THREE.Scene();

    {
      const light = new THREE.AmbientLight(0xffffff);
      scene.add(light);
    }

    {
      const geometry = new THREE.SphereGeometry(0.1, 24, 24)
      const material = new THREE.MeshPhongMaterial({color: 0xaa8844})
      const sphere = new THREE.Mesh(geometry, material);
      sphere.position.set(0, 0, 0);
      scene.add(sphere);
    }

    {
      this.orbitDataService.getPosition(1621827699, -1826843336, 2035226060, 0)
        .then(position => {
          const geometry = new THREE.SphereGeometry(0.1, 24, 24)
          const material = new THREE.MeshPhongMaterial({color: 0xff0000});
          const sphere = new THREE.Mesh(geometry, material);
          sphere.position.set(position.x / this._AU, position.y / this._AU, position.z / this._AU)
          scene.add(sphere)
        })
    }

    {
      this.orbitDataService.getPosition(1621827699, -1826843336, -154475081, 0)
        .then(position => {
          const geometry = new THREE.SphereGeometry(0.1, 24, 24)
          const material = new THREE.MeshPhongMaterial({color: 0xffff00});
          const sphere = new THREE.Mesh(geometry, material);
          sphere.position.set(position.x / this._AU, position.y / this._AU, position.z / this._AU)
          scene.add(sphere)
        })
    }

    {
      this.orbitDataService.getPosition(1621827699, -1826843336, 159569841, 0)
        .then(position => {
          const geometry = new THREE.SphereGeometry(0.1, 24, 24)
          const material = new THREE.MeshPhongMaterial({color: 0x00ff00});
          const sphere = new THREE.Mesh(geometry, material);
          sphere.position.set(position.x / this._AU, position.y / this._AU, position.z / this._AU)
          scene.add(sphere)
        })
    }

    {
      this.orbitDataService.getEarth(0)
        .then(position => {
          const geometry = new THREE.SphereGeometry(0.1, 24, 24)
          const material = new THREE.MeshPhongMaterial({color: 0x0000ff});
          const sphere = new THREE.Mesh(geometry, material);
          sphere.position.set(position.x / this._AU, position.y / this._AU, position.z / this._AU)
          scene.add(sphere)
        })
    }

    {
      const geometry = new THREE.TubeGeometry(this.orbitDataService.getOrbitPath(0), 128, 0.01, 8, true);
      const material = new THREE.MeshPhongMaterial({color: 0x777777});
      const tube = new THREE.Mesh(geometry, material);
      scene.add(tube);
    }

    {
      const geometry = new THREE.TubeGeometry(this.orbitDataService.getOrbitPath(1), 128, 0.01, 8, true);
      const material = new THREE.MeshPhongMaterial({color: 0x777777});
      const tube = new THREE.Mesh(geometry, material);
      scene.add(tube);
    }

    {
      const geometry = new THREE.TubeGeometry(this.orbitDataService.getOrbitPath(2), 128, 0.01, 8, true);
      const material = new THREE.MeshPhongMaterial({color: 0x777777});
      const tube = new THREE.Mesh(geometry, material);
      scene.add(tube);
    }

    {
      const geometry = new THREE.TubeGeometry(this.orbitDataService.getOrbitPath(3), 128, 0.01, 8, true);
      const material = new THREE.MeshPhongMaterial({color: 0x777777});
      const tube = new THREE.Mesh(geometry, material);
      scene.add(tube);
    }

    const axesHelper = new THREE.AxesHelper(5);
    scene.add(axesHelper);

    // const controls = new OrbitControls(camera, renderer.domElement);
    // controls.enableDamping = true;
    // controls.dampingFactor = 0.05;
    // controls.screenSpacePanning = false;
    // controls.minDistance = 0.1;
    // controls.maxDistance = 100;

    const controls = new TrackballControls(camera, renderer.domElement);
    // controls.dynamicDampingFactor = 0.05;
    controls.maxDistance = 20;
    controls.minDistance = 1;
    controls.rotateSpeed = 2;



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

