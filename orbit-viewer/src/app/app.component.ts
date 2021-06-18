import {Component, OnInit} from '@angular/core';
import * as THREE from 'three';
import {BoxGeometry} from 'three';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'Orbit Viewer';

  ngOnInit(): void {
    // const canvas = <HTMLCanvasElement> document.getElementById('orbitplot');
    // const renderer = new THREE.WebGLRenderer({canvas});
    //
    // const fov = 75;
    // const aspect = 2;
    // const near = 0.1;
    // const far = 5;
    // const camera = new THREE.PerspectiveCamera(fov, aspect, near, far);
    // camera.position.z = 2;
    //
    // const scene = new THREE.Scene();
    //
    // {
    //   const color = 0xFFFFFF;
    //   const intensity = 1;
    //   const light = new THREE.DirectionalLight(color, intensity);
    //   light.position.set(-1, 2, 4);
    //   scene.add(light);
    // }
    //
    // const boxWidth = 1;
    // const boxHeight = 1;
    // const boxDepth = 1;
    // const geometry = new THREE.BoxGeometry(boxWidth, boxHeight, boxDepth);
    //
    // function makeInstance(geometry: BoxGeometry, color: number, x: number) {
    //   const material = new THREE.MeshPhongMaterial({color});
    //   const cube = new THREE.Mesh(geometry, material);
    //   scene.add(cube);
    //   cube.position.x = x;
    //   return cube;
    // }
    //
    // const cubes = [
    //   makeInstance(geometry, 0x44aa88, 0),
    //   makeInstance(geometry, 0x8844aa, -2),
    //   makeInstance(geometry, 0xaa8844, 2),
    // ];
    //
    // function resizeRendererToDisplaySize(renderer : THREE.WebGLRenderer) {
    //   const pixelRatio = window.devicePixelRatio;
    //   const width = canvas.clientWidth * pixelRatio | 0;
    //   const height = canvas.clientHeight * pixelRatio | 0;
    //   const needResize = canvas.width !== width || canvas.height !== height;
    //   if (needResize) renderer.setSize(width, height, false);
    //   return needResize;
    // }
    //
    // function render(time: number) {
    //   time *= 0.001;
    //
    //   if (resizeRendererToDisplaySize(renderer)) {
    //     camera.aspect = canvas.clientWidth / canvas.clientHeight;
    //     camera.updateProjectionMatrix();
    //   }
    //
    //   cubes.forEach((cube, ndx) => {
    //     const speed = 1 + ndx * 0.1;
    //     const rot = time * speed;
    //     cube.rotation.x = rot;
    //     cube.rotation.y = rot;
    //   });
    //
    //   renderer.render(scene, camera);
    //   requestAnimationFrame(render);
    // }
    // requestAnimationFrame(render);
  }
}
