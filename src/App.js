import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';
import locationData from './locations.json';

const SERVICE_KEY = process.env.REACT_APP_SERVICE_KEY;
const DATA_TYPE = "JSON";

function getPreviousDate() {
  const currentDate = new Date();
  currentDate.setDate(currentDate.getDate() - 1);
  return currentDate.toISOString().slice(0, 10).replace(/-/g, "");
}

async function fetchWeatherData(NX, NY) {
  const BASE_DATE = getPreviousDate();
  const BASE_TIME = "2300";

  const url = `http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?ServiceKey=${SERVICE_KEY}&pageNo=1&numOfRows=290&dataType=${DATA_TYPE}&base_date=${BASE_DATE}&base_time=${BASE_TIME}&nx=${NX}&ny=${NY}`;

  try {
    const response = await axios.get(url);
    return response.data;
  } catch (error) {
    console.error(error);
  }
}

function extractChartData(weatherData, category) {
  const { response } = weatherData;
  const { body } = response;
  const { items } = body;
  const { item } = items;

  return item
    .filter(row => row.category === category && row.baseTime === "2300")
    .map(row => ({
      name: row.fcstTime,
      value: row.fcstValue
    }));
}

function WeatherChart({ data, title, yUnit }) {
  return (
    <div>
      <h2>{title}</h2>
      <LineChart
        width={700}
        height={400}
        data={data}
        margin={{ top: 50, right: 50, left: 20, bottom: 5 }}
      >
        <XAxis dataKey="name" label={{ value: "[시]", position: "right", offset: 20 }} />
        <YAxis
          label={{
            value: "[" + yUnit + "]",
            position: "top",
            angle: 0,
            offset: 20,
            dy: 0,
          }}
        />
        <CartesianGrid strokeDasharray="3 3" />
        <Tooltip />
        <Legend />
        <Line type="monotone" dataKey="value" stroke="#8884d8" activeDot={{ r: 8 }} />
      </LineChart>
    </div>
  );
}


function getNxNy(city, district, neighborhood) {
  const location = locationData.find(
    (loc) =>
      loc.city === city &&
      loc.district === district &&
      loc.neighborhood === neighborhood
  );

  if (location) {
    return { nx: location.nx, ny: location.ny };
  } else {
    throw new Error("Location not found");
  }
}

const uniqueCities = [...new Set(locationData.map((loc) => loc.city))];
const uniqueDistricts = (city) => [
  ...new Set(
    locationData
      .filter((loc) => loc.city === city)
      .map((loc) => loc.district)
  ),
];
const uniqueNeighborhoods = (city, district) => [
  ...new Set(
    locationData
      .filter((loc) => loc.city === city && loc.district === district)
      .map((loc) => loc.neighborhood)
  ),
];

function App() {
  const [popChartData, setPopChartData] = useState([]);
  const [tmpChartData, setTmpChartData] = useState([]);
  const [wsdChartData, setWsdChartData] = useState([]);
  const [isLocationFound, setIsLocationFound] = useState(true);
  const [chartData, setChartData] = useState([]);
  const [city, setCity] = useState("");
  const [district, setDistrict] = useState("");
  const [neighborhood, setNeighborhood] = useState("");

  async function fetchData(nx, ny) {
    const weatherData = await fetchWeatherData(nx, ny);
    setPopChartData(extractChartData(weatherData, "POP"));
    setTmpChartData(extractChartData(weatherData, "TMP"));
    setWsdChartData(extractChartData(weatherData, "WSD"));
  }

  function handleSubmit(event) {
    event.preventDefault();
    try {
      const { nx, ny } = getNxNy(city, district, neighborhood);
      setIsLocationFound(true);
      fetchData(nx, ny);
    } catch (error) {
      console.error(error);
      setIsLocationFound(false);
    }
  }   

  function handleLocationChange() {
    setIsLocationFound(true);
  }  

  console.log(SERVICE_KEY);
  return (
    <div className="App">
      <h1>Weather Visualizer</h1>
      <form onSubmit={handleSubmit}>
        <label>
          시/도:
          <select value={city} onChange={(e) => {setCity(e.target.value); handleLocationChange();}}>
            <option value="">Select City</option>
            {uniqueCities.map((city) => (
              <option key={city} value={city}>
                {city}
              </option>
            ))}
          </select>
        </label>
        <label>
          시/군/구:
          <select
            value={district}
            onChange={(e) => {setDistrict(e.target.value); handleLocationChange();}}
            disabled={!city}
          >
            <option value="">Select District</option>
            {uniqueDistricts(city).map((district) => (
              <option key={district} value={district}>
                {district}
              </option>
            ))}
          </select>
        </label>
        <label>
          읍/면/동:
          <select
            value={neighborhood}
            onChange={(e) => {setNeighborhood(e.target.value); handleLocationChange();}}
            disabled={!district}
          >
            <option value="">Select Neighborhood</option>
            {uniqueNeighborhoods(city, district).map((neighborhood) => (
              <option key={neighborhood} value={neighborhood}>
                {neighborhood}
              </option>
            ))}
          </select>
        </label>
        <input type="submit" value="Submit" disabled={!isLocationFound} />
      </form>
      {isLocationFound ? (
        <>
        <WeatherChart data={popChartData} title="강수확률" yUnit="%" />
        <WeatherChart data={tmpChartData} title="기온" yUnit="°" />
        <WeatherChart data={wsdChartData} title="풍속" yUnit="m/s" />
      </>      
      ) : (
        <p>No forecast points found. Please select a valid location.</p>
      )}
    </div>
  );
}


export default App;
