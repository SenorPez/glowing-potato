export interface Planet {
  name: string;
  mass: number;
  radius: number;
  semimajorAxis: number;
  eccentricity: number;
  inclination: number;
  longitudeOfAscendingNode: number;
  argumentOfPeriapsis: number;
  trueAnomalyAtEpoch: number;

  starGM: number
  GM: number
}
