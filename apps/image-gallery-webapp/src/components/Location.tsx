import { DivIcon } from 'leaflet';
import 'leaflet/dist/leaflet.css';

import { MapContainer, Marker, TileLayer } from 'react-leaflet';
import { Location as LocationValue } from '~/types';

export interface LocationProps {
  location: LocationValue;
}

export function Location({ location }: LocationProps) {
  const position = [location.lat, location.lon] as [number, number];
  return (
    <div className={'relative h-[200px] w-[100%] bg-white md:h-[300px] lg:h-[500px]'}>
      <MapContainer
        zoom={22}
        minZoom={2}
        center={position}
        scrollWheelZoom={false}
        className={`h-[100%] w-full`}>
        <TileLayer
          url="https://tiles.stadiamaps.com/tiles/alidade_smooth/{z}/{x}/{y}{r}.png"
          attribution='&copy; <a href="https://www.stadiamaps.com/" target="_blank">Stadia Maps</a> &copy; <a href="https://openmaptiles.org/" target="_blank">OpenMapTiles</a> &copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        />
        <Marker position={position} icon={markerIcon} />
      </MapContainer>
    </div>
  );
}

const markerIcon = new DivIcon({
  iconSize: [42, 42],
  className: 'rounded-full border-8 border-rose-500 bg-neutral-100 shadow-md shadow-neutral-400',
});
