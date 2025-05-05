const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const { HTTP_STATUS } = require('../constants/status-code.js');
const AppError = require('../utils/app-error.js');
const db = require('../config/db.config');

exports.login = async (req, res, next) => {
  const { email, password } = req.body;

  try {
    // Query the database for the user
    const [rows] = await db.pool.execute(
      'SELECT * FROM users WHERE email = ?',
      [email]
    );

    if (rows.length === 0) {
      return next(new AppError(HTTP_STATUS.NOT_FOUND, 'failed', 'Không tìm thấy người dùng', []), req, res, next);
    }
    const user = rows[0];
    // Compare password
    const passwordMatch = await bcrypt.compare(password, user.password);
    if (!passwordMatch) {
      return res.status(200).json({
        code: 401,
        message: 'Mật khẩu không chính xác'
      });
    }

    // Create a JWT token
    const token = jwt.sign(
      { id: user.id, email: user.email },
      process.env.JWT_SECRET_KEY,
      { expiresIn: '1h' }
    );

    // Optionally store user session (if required)
    req.session.user = {
      id: user.id,
      email: user.email
    };

    // Return response with token
    res.status(200).json({
      status: 'success',
      token: token,
      data: {
        id: user.id,
        fullname: user.name,
        // zalo: user.zalo,
        // email: user.email,
        // address: user.address,
        // fbUrl: user.fbUrl,
        avatar: user.avatar,
        createdAt: user.createdAt,
        // updatedAt: user.updatedAt,
      },
      message: 'Đăng nhập thành công',
    });
  } catch (error) {
    console.error('Error in login function:', error);
    res.status(500).json({ error: error.message });
  }
};

exports.register = async (req, res, next) => {
  const { fullname, email, password } = req.body;
  try {
    const [emailCheck] = await db.pool.execute('SELECT * FROM users WHERE email = ?', [email]);
    if (emailCheck.length > 0) {
      return res.status(200).json({
        code: 401,
        message: 'Số điện thoại đã được đăng ký'
      });
    }
    const hashedPassword = await bcrypt.hash(password, 8);
    const createdAt = new Date();
    const username = email.split('@')[0]; // Lấy phần trước dấu '@' trong email
    await db.pool.execute('INSERT INTO users (username, full_name, email, password, created_at) VALUES (?, ?, ?, ?, ?)', [username, fullname, email,  hashedPassword, createdAt]);
    const user = { email, createdAt };

    res.status(201).json({
      code: 201,
      status: 'success',
      data: user,
      message: 'Đăng ký thành công',
    });

  } catch (error) {
    console.error('Error in register function:', error);
    res.status(500).json({ error: error.message });
    next(error);
  }
}

exports.logout = async (req, res, next) => { 
  
  try {
    if (req.session.user) {
      await db.pool.execute(
        'UPDATE users SET status = 0 WHERE id = ?',
        ['offline', req.session.user.id]
      );
      req.session.destroy();
      res.status(200).json({ message: 'Đăng xuất thành công' });
    } else {
      res.status(200).json({ message: 'Bạn chưa đăng nhập' });
    }
  } catch (error) {
    console.error('Error in logout function:', error);
    res.status(500).json({ error: error.message });
  }
}