import {Component, Input, OnInit} from '@angular/core';
import {OrbitdataService} from "../orbitdata.service";
import * as THREE from "three";

@Component({
  selector: 'app-time',
  templateUrl: './time.component.html',
  styleUrls: ['./time.component.css']
})
export class TimeComponent implements OnInit {
  private solarRadius: number = 800240.666; // Solar radius in km. 1 Solar Radius = 1 axis unit.
  // TODO: Add solar radius to API?
  private zScale: number = 20; // z Scale
  private planetScale: number = 1000;

  @Input() scene !: THREE.Scene;


  constructor(private orbitDataService: OrbitdataService) { }

  ngOnInit(): void {
  }

  clickNext() {
    const t0 = Date.now() / 1000;
    this.orbitDataService.getCachedPlanetPosition(2035226060, t0)
      .then(position => {
        position.z *= this.zScale;
        position.divideScalar(this.solarRadius * 1000);
        const planet_radius = 2164.0
        const geometry = new THREE.SphereGeometry(planet_radius / this.solarRadius, 24, 24);
        const material = new THREE.MeshBasicMaterial({color: 0xFF0000});
        const sphere = new THREE.Mesh(geometry, material);
        sphere.position.set(position.x, position.y, position.z);
        this.scene.add(sphere);

        const transparent_geometry = new THREE.SphereGeometry(planet_radius / this.solarRadius * this.planetScale, 24, 24);
        const transparent_material = new THREE.MeshStandardMaterial({
          color: 0xFF0000,
          transparent: true,
          opacity: 0.25
        });
        const transparent_sphere = new THREE.Mesh(transparent_geometry, transparent_material);
        transparent_sphere.position.set(position.x, position.y, position.z);
        this.scene.add(transparent_sphere);
      })
  }
}
