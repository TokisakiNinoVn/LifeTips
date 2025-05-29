require('dotenv').config();
const cors = require('cors');
const express = require('express');
const session = require('express-session');
const bodyParser = require('body-parser');

const { authorizeRole, authenticate, loginRequired } = require("./src/middlewares/middleware")
const authRoutes = require('./src/routes/public/index');
const privateRoutes = require('./src/routes/private/index');
const responseMiddleware = require('./src/middlewares/response-middleware');

const app = express();

// Middleware
app.use(bodyParser.json());

app.use(cors({
  origin: '*',
  methods: ['GET', 'POST', 'PUT', 'DELETE'],
  allowedHeaders: ['Content-Type', 'Authorization',],
  credentials: true,
}));

app.use(session({
  secret: process.env.SESSION_SECRET,
  resave: false,
  saveUninitialized: false,
  cookie: {
    secure: false,
    httpOnly: true,
    maxAge: 1000 * 60 * 60
  }
}));

// Logging middleware
app.use((req, res, next) => {
  // thêm thời gian vào logging
  const currentTime = new Date().toISOString();
  console.log(`>> [${currentTime}][${req.method}] - URL: ${req.url}`);
  next();
});

// api get mặc định 
app.get('/api', (req, res) => {
  res.status(200).json({
    status: "Success",
    message: "The API LifeTips Server is running!"
  });
});

// Định nghĩa các route chính
app.use('/api/public', authRoutes);
app.use('/api/private', loginRequired, privateRoutes);

// app.use(express.static("views"));
app.use(express.static('public'));

// Routes
app.get('/', function(request, response) {
  console.log('>> Received request at /'); // Logging
  response.set('Cache-Control', 'no-store');
  response.status(200).json({
    status: "Success",
    message: "The API LifeTips Server is running!"
  });
});

app.get('/test', function(request, response) {
  response.status(200).json({
    message: "The API LifeTips Server is running!"
  });
});
app.get('/api/private/test', function(request, response) {
  response.status(200).json({
    message: "Hehe, You are on \" /api/private/test\" API"
  });
});
app.get('/api/public/test', function(request, response) {
  response.status(200).json({
    message: "Hehe, You are on \" /api/public/test\" API"
  });
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`>> Server running on port ${PORT}`);
});

app.get('/api/check-session', (req, res) => {
  if (req.session.user) {
    res.json({ loggedIn: true, user: req.session.user });
  } else {
    res.json({ loggedIn: false });
  }
});

app.use(responseMiddleware.format);