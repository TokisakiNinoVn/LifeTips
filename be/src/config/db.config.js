const mysql = require('mysql2');
require('dotenv').config();

const pool = mysql.createPool({
  host: process.env.DB_HOST,
  user: process.env.DB_USER,
  password: process.env.DB_PASS,
  database: process.env.DB_NAME,
}).promise();


const { Sequelize } = require('sequelize');
const sequelize = new Sequelize(process.env.DB_NAME, process.env.DB_USER, process.env.DB_PASS, {
  host: process.env.DB_HOST,
  dialect: 'mysql',
});

async function testConnection() {
  try {
    // Kiểm tra kết nối pool
    await pool.getConnection();
    // console.log('MySQL Pool connected successfully.');

    // Kiểm tra kết nối Sequelize
    await sequelize.authenticate();
    console.log('>> Connected database successfully! ✌️');
  } catch (error) {
    console.error('Unable to connect to the database:', error);
  }
}

if (process.env.NODE_ENV === 'development') {
  sequelize.sync({ force: true }).then(() => {
    console.log('Drop and re-sync db.');
  });
}

testConnection();

const db = {
  pool,
  Sequelize,
  sequelize
};

module.exports = db;
