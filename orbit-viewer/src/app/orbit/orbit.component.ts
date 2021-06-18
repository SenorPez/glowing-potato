import { Component, OnInit } from '@angular/core';
import * as THREE from 'three';
import {OrbitdataService} from "../orbitdata.service";
import {OrbitControls} from "three/examples/jsm/controls/OrbitControls";

@Component({
  selector: 'app-orbit',
  templateUrl: './orbit.component.html',
  styleUrls: ['./orbit.component.css']
})
export class OrbitComponent implements OnInit {

  constructor(private orbitDataService : OrbitdataService) { }

  ngOnInit(): void {
    const canvas = <HTMLCanvasElement>document.getElementById('orbitplot');
    const renderer = new THREE.WebGLRenderer({canvas})

    // const camera = new THREE.PerspectiveCamera();
    // camera.position.z = 10;

    const camera = new THREE.OrthographicCamera(-5, 5, 5, -5);
    camera.position.z = 20;

    const scene = new THREE.Scene();

    {
      const color = 0xFFFFFF;
      const intensity = 1;
      const light = new THREE.DirectionalLight(color, intensity);
      light.position.set(2, 2, 2);
      scene.add(light);
    }

    // {
    //   const geometry = new THREE.PlaneGeometry(10, 10);
    //   const material = new THREE.MeshPhongMaterial({color: 0xffff00, side: THREE.DoubleSide});
    //   const plane = new THREE.Mesh(geometry, material);
    //   scene.add(plane);
    // }

    {
      const geometry = new THREE.SphereGeometry(0.25);
      const material = new THREE.MeshPhongMaterial({color: 0xaa8844})
      const sphere = new THREE.Mesh(geometry, material);
      sphere.position.set(0, 0, 0);
      scene.add(sphere);
    }

    {
      const geometry = new THREE.SphereGeometry(0.25)
      const material = new THREE.MeshPhongMaterial({color: 0x00ff00});
      const sphere = new THREE.Mesh(geometry, material);
      const positions = this.orbitDataService.getPosition();
      sphere.position.set(positions[0], positions[1], positions[2]);
      scene.add(sphere);
    }

    {
      const geometry = new THREE.TubeGeometry(this.orbitDataService.getOrbitPath(), 128, 1, 8, true);
      const material = new THREE.MeshPhongMaterial({color: 0x777777});
      const tube = new THREE.Mesh(geometry, material);
      console.log(tube);
      scene.add(tube);
    }

    const axesHelper = new THREE.AxesHelper(5);
    scene.add(axesHelper);

    const controls = new OrbitControls(camera, renderer.domElement);
    controls.enableDamping = true;
    controls.dampingFactor = 0.05;
    controls.screenSpacePanning = false;
    controls.minDistance = 0.1;
    controls.maxDistance = 100;
    // controls.maxPolarAngle = Math.PI / 2;


    function render(time: number) {
      const pixelRatio = window.devicePixelRatio;
      const width = canvas.clientWidth * pixelRatio | 0;
      const height = canvas.clientHeight * pixelRatio | 0;
      if (canvas.width !== width || canvas.height !== height) {
        renderer.setSize(width, height, false);
        // camera.aspect = canvas.clientWidth / canvas.clientHeight;
        camera.updateProjectionMatrix();
      }

      renderer.render(scene, camera);
      requestAnimationFrame(render);
      controls.update();
    }

    requestAnimationFrame(render);


  }
}

