import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';
import locationData from './locations.json';

const SERVICE_KEY = "#### API SERVICE KEY ####";
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

function WeatherChart({ data, title }) {
  return (
    <div>
      <h2>{title}</h2>
      <LineChart
        width={500}
        height={300}
        data={data}
        margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
      >
        <XAxis dataKey="name" />
        <YAxis />
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
    const { nx, ny } = getNxNy(city, district, neighborhood);
    fetchData(nx, ny);
  }  

  return (
    <div className="App">
      <h1>Weather Visualization</h1>
      <form onSubmit={handleSubmit}>
        <label>
          시/도:
          <select value={city} onChange={(e) => setCity(e.target.value)}>
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
            onChange={(e) => setDistrict(e.target.value)}
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
            onChange={(e) => setNeighborhood(e.target.value)}
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
        <input type="submit" value="Submit" />
      </form>
      <WeatherChart data={popChartData} title="강수확률" />
      <WeatherChart data={tmpChartData} title="기온" />
      <WeatherChart data={wsdChartData} title="풍속" />
    </div>
  );
}


export default App;
