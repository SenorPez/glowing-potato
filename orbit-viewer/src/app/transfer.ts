import {Vector3} from "three";

export interface Transfer {
  position: Vector3;
  target: Vector3;
  velocity: Vector3;
  name: string;
  mu: number;
  startTime: number;
}
