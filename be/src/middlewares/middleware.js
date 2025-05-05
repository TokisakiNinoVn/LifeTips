// middleware.js
require('dotenv').config();
const jwt = require('jsonwebtoken');

// Middleware xác thực quyền truy cập dựa trên vai trò
exports.authorizeRole = (role) => {
  return (req, res, next) => {
    try {
      if (req.session.user && req.session.user.role === role) {
        next();
      } else {
        res.status(403).json({ message: "Access forbidden: Insufficient rights" });
      }
    } catch (error) {
      console.error("Error in authorizeRole middleware:", error);
      res.status(500).json({ error: "Internal Server Error" });
    }
  };
};

exports.authenticate = (req, res, next) => {
  try {
    // Check if Authorization header is present
    const token = req.headers['authorization']?.split(' ')[1];

    if (!token) {
      return res.status(403).json({ error: 'Token is required' });
    }

    // Verify and decode the token
    jwt.verify(token, process.env.JWT_SECRET_KEY, (err, decoded) => {
      if (err) {
        return res.status(403).json({ error: 'Unauthorized access' });
      }

      // Attach decoded user info to request object
      req.user = decoded;
      next();
    });
  } catch (error) {
    console.error('Error in authenticate middleware:', error);
    res.status(500).json({ error: 'Internal Server Error' });
  }
};


// Middleware kiểm tra đăng nhập
// exports.loginRequired = (req, res, next) => {
//   if (req.session && req.session.user) {
//     return next();
//   }

//   // If session is not found, fall back to checking for JWT token
//   const token = req.headers['authorization']?.split(' ')[1]; // Bearer <token>
//   console.log(">> token: ", token);
//   if (!token) {
//     return res.status(401).json({ message: 'line 58: Login required' });
//   }

//   // Verify token if session is not available
//   jwt.verify(token, process.env.JWT_SECRET_KEY, (err, decoded) => {
//     console.log("JWT_SECRET_KEY: ", process.env.JWT_SECRET_KEY);
//     if (err) {
//       return res.status(401).json({ message: 'line 64: Login required' });
//     }

//     req.user = decoded;
//     // const decoded = jwt.decode(token, { complete: true });
//     console.log('>> ',decoded);

//     next();
//   });
// };

exports.loginRequired = (req, res, next) => {
  if (req.session && req.session.user) {
    return next();
  }

  const authHeader = req.headers.authorization;
  const jwtSecret = process.env.JWT_SECRET_KEY;
  // console.log("JWT Secret:", jwtSecret);

  if (!authHeader || !authHeader.startsWith('Bearer ')) {
    return res.status(401).json({ message: 'Không có token trong header hoặc token không hợp lệ' });
  }

  // Lấy token và loại bỏ dấu ngoặc kép nếu có
  let token = authHeader.split(' ')[1];
  token = token.replace(/^"|"$/g, ''); // Loại bỏ dấu `"` ở đầu và cuối token
  // console.log("Token:", token);

  if (!token) {
    return res.status(401).json({ message: 'line 58: Login required' });
  }

  try {
    const decoded = jwt.verify(token, jwtSecret);
    req.user = decoded;
    next();
  } catch (err) {
    console.error(err);
    return res.status(400).json({ code: 400, status: 'JsonWebTokenError', message: err.message });
  }
};
