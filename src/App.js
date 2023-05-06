import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';

const SERVICE_KEY = "#### API SERVICE KEY ####";
const DATA_TYPE = "JSON";

function getPreviousDate() {
  const currentDate = new Date();
  currentDate.setDate(currentDate.getDate() - 1);
  return currentDate.toISOString().slice(0, 10).replace(/-/g, "");
}

async function fetchWeatherData() {
  const BASE_DATE = getPreviousDate();
  const BASE_TIME = "2300";
  const NX = "55";
  const NY = "128";

  const url = `http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?ServiceKey=${SERVICE_KEY}&pageNo=1&numOfRows=290&dataType=${DATA_TYPE}&base_date=${BASE_DATE}&base_time=${BASE_TIME}&nx=${NX}&ny=${NY}`;

  try {
    const response = await axios.get(url);
    return response.data;
  } catch (error) {
    console.error(error);
  }
}

function extractChartData(weatherData) {
  const { response } = weatherData;
  const { body } = response;
  const { items } = body;
  const { item } = items;

  return item
    .filter(row => row.category === "POP" && row.baseTime === "2300")
    .map(row => ({
      name: row.fcstDate,
      value: row.fcstValue
    }));
}

function WeatherChart({ data }) {
  return (
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
  );
}

function App() {
  const [chartData, setChartData] = useState([]);

  useEffect(() => {
    async function fetchData() {
      const weatherData = await fetchWeatherData();
      const data = extractChartData(weatherData);
      setChartData(data);
    }
    fetchData();
  }, []);

  return (
    <div className="App">
      <h1>Weather Visualization</h1>
      <WeatherChart data={chartData} />
    </div>
  );
}

export default App;