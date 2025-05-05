// const db = require('../config/db.config'); // Kết nối MySQL

// const User = db.sequelize.define('User', {
//   id: {
//     type: db.Sequelize.INTEGER,
//     primaryKey: true,
//     autoIncrement: true
//   },
//   username: { // Thêm trường username
//     type: db.Sequelize.STRING,
//     allowNull: false,
//     unique: true // Đảm bảo rằng username là duy nhất
//   },
//   name: {
//     type: db.Sequelize.STRING,
//     allowNull: false
//   },
//   email: {
//     type: db.Sequelize.STRING,
//     allowNull: false,
//     unique: true
//   },
//   phone: {
//     type: db.Sequelize.STRING
//   },
//   address: {
//     type: db.Sequelize.STRING
//   },
//   role: {
//     type: db.Sequelize.ENUM('player', 'admin'),
//     defaultValue: 'player'
//   },
//   gender: {
//     type: db.Sequelize.ENUM('nam', 'nữ', 'khác'),
//     allowNull: true
//   },
//   password: {
//     type: db.Sequelize.STRING,
//     allowNull: false
//   }
// });

// module.exports = User;
