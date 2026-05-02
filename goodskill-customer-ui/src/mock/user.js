import Mock from 'mockjs'

const { Random } = Mock

const mockData = [
  {
    url: '/api/auth/customer/userinfo',
    method: 'get',
    response: () => {
      return {
        code: 0,
        msg: null,
        data: {
          userId: 1,
          username: '测试用户',
          emailAddr: 'test@example.com',
          mobile: '17612185982',
          avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20user%20avatar%20portrait&image_size=square'
        }
      }
    }
  },
  {
    url: '/api/auth/customer/userinfo',
    method: 'put',
    response: (req) => {
      const body = req.body
      return {
        code: 0,
        msg: null,
        data: {
          userId: 1,
          username: '测试用户',
          emailAddr: body.emailAddr || 'test@example.com',
          mobile: '17612185982',
          avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20user%20avatar%20portrait&image_size=square'
        }
      }
    }
  },
  {
    url: '/api/auth/customer/avatar',
    method: 'put',
    response: (req) => {
      const { avatarUrl } = req.query
      return {
        code: 0,
        msg: null,
        data: {
          userId: 1,
          username: '测试用户',
          emailAddr: 'test@example.com',
          mobile: '17612185982',
          avatar: avatarUrl || 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20user%20avatar%20portrait&image_size=square'
        }
      }
    }
  },
  {
    url: '/api/common/oss/upload',
    method: 'post',
    response: () => {
      return {
        code: 0,
        msg: null,
        data: {
          url: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20user%20avatar%20portrait&image_size=square',
          filename: `avatar_${Date.now()}.jpg`,
          size: 102400
        }
      }
    }
  }
]

export default mockData
