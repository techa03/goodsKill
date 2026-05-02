import request from '../utils/request'

// 强制使用mock数据
const USE_MOCK = true

console.log('USE_MOCK:', USE_MOCK)

const mockData = {
  login: {
    success: {
      code: 200,
      data: {
        accessToken: 'mock-access-token',
        refreshToken: 'mock-refresh-token',
        tokenType: 'Bearer',
        accessExpireSeconds: 30,
        refreshExpireSeconds: 604800
      }
    },
    fail: {
      code: 500,
      message: '用户名或密码错误'
    }
  },
  goodsList: {
    code: 200,
    data: {
      records: [
        {
          id: 1001,
          goodsId: 1001,
          goodsName: 'iPhone 15 Pro',
          goodsTitle: '苹果最新款手机',
          seckillPrice: 7999,
          goodsPrice: 9999,
          stockCount: 100,
          endTime: new Date(Date.now() + 3600000).toISOString(),
          goodsImg: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=iPhone%2015%20Pro%20product%20photo&image_size=square'
        },
        {
          id: 1002,
          goodsId: 1002,
          goodsName: 'MacBook Pro',
          goodsTitle: '苹果最新款笔记本电脑',
          seckillPrice: 12999,
          goodsPrice: 15999,
          stockCount: 50,
          endTime: new Date(Date.now() + 7200000).toISOString(),
          goodsImg: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=MacBook%20Pro%20laptop%20product%20photo&image_size=square'
        },
        {
          id: 1003,
          goodsId: 1003,
          goodsName: 'AirPods Pro',
          goodsTitle: '苹果无线降噪耳机',
          seckillPrice: 1599,
          goodsPrice: 1999,
          stockCount: 200,
          endTime: new Date(Date.now() + 1800000).toISOString(),
          goodsImg: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=AirPods%20Pro%20wireless%20earbuds%20product%20photo&image_size=square'
        }
      ],
      total: 3,
      pages: 1
    }
  },
  orderList: {
    code: 200,
    data: {
      records: [
        {
          id: 'ORD001',
          goodsName: 'iPhone 15 Pro',
          goodsTitle: '苹果最新款手机 256GB 深空灰',
          goodsImg: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=iPhone%2015%20Pro%20product%20photo&image_size=square',
          seckillPrice: 7999,
          state: 1,
          status: 1,
          stateDesc: '待支付',
          createTime: new Date().toISOString(),
          userPhone: '13800138000'
        },
        {
          id: 'ORD002',
          goodsName: 'MacBook Pro',
          goodsTitle: '苹果最新款笔记本电脑 14英寸 M3 Pro',
          goodsImg: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=MacBook%20Pro%20laptop%20product%20photo&image_size=square',
          seckillPrice: 12999,
          state: 2,
          status: 2,
          stateDesc: '已完成',
          createTime: new Date(Date.now() - 86400000).toISOString(),
          userPhone: '13800138000'
        },
        {
          id: 'ORD003',
          goodsName: 'AirPods Pro',
          goodsTitle: '苹果无线降噪耳机 第二代',
          goodsImg: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=AirPods%20Pro%20wireless%20earbuds%20product%20photo&image_size=square',
          seckillPrice: 1599,
          state: 3,
          status: 3,
          stateDesc: '已取消',
          createTime: new Date(Date.now() - 172800000).toISOString(),
          userPhone: '13800138000'
        }
      ],
      total: 3,
      pages: 1
    }
  },
  orderDetail: {
    code: 200,
    data: {
      id: 'ORD001',
      goodsName: 'iPhone 15 Pro',
      goodsTitle: '苹果最新款手机 256GB 深空灰',
      goodsImg: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=iPhone%2015%20Pro%20product%20photo&image_size=square',
      seckillPrice: 7999,
      state: 1,
      stateDesc: '待支付',
      createTime: new Date().toISOString(),
      userPhone: '13800138000'
    }
  }
}

export const api = {
  login: (data) => {
    if (USE_MOCK) {
      return new Promise((resolve) => {
        setTimeout(() => {
          if (data.username === 'test03' && data.password === 'aa123456') {
            resolve(mockData.login.success)
          } else {
            resolve(mockData.login.fail)
          }
        }, 500)
      })
    }
    return request({
      url: '/auth/login',
      method: 'post',
      data
    })
  },

  getGoodsList: (params) => {
    if (USE_MOCK) {
      return new Promise((resolve) => {
        setTimeout(() => {
          resolve(mockData.goodsList)
        }, 300)
      })
    }
    return request({
      url: '/seckill/getSeckillList',
      method: 'get',
      params
    })
  },

  getGoodsDetail: (id) => {
    if (USE_MOCK) {
      return new Promise((resolve) => {
        setTimeout(() => {
          const goods = mockData.goodsList.data.records.find(item => item.id === Number(id))
          resolve({
            code: 200,
            data: goods || null
          })
        }, 300)
      })
    }
    return request({
      url: '/seckill/getById',
      method: 'get',
      params: { seckillId: id }
    })
  },

  exportSeckillUrl: (seckillId) => {
    if (USE_MOCK) {
      return new Promise((resolve) => {
        setTimeout(() => {
          resolve({
            code: 200,
            data: {
              exposed: true,
              md5: 'mock-md5-hash'
            }
          })
        }, 300)
      })
    }
    return request({
      url: '/seckill/exportSeckillUrl',
      method: 'post',
      params: { seckillId }
    })
  },

  executeSeckill: (data) => {
    if (USE_MOCK) {
      return new Promise((resolve) => {
        setTimeout(() => {
          const orderId = 'ORD' + Date.now()
          resolve({
            code: 200,
            data: {
              orderId: orderId
            },
            message: '秒杀成功'
          })
        }, 500)
      })
    }
    return request({
      url: '/seckill/customer/execute',
      method: 'post',
      params: {
        seckillId: Number(data.seckillId),
        userPhone: data.userPhone,
        md5: data.md5
      }
    })
  },

  getOrderList: (params) => {
    if (USE_MOCK) {
      return new Promise((resolve) => {
        setTimeout(() => {
          resolve(mockData.orderList)
        }, 300)
      })
    }
    return request({
      url: '/order/list',
      method: 'get',
      params
    })
  },

  getOrderDetail: (id) => {
    if (USE_MOCK) {
      return new Promise((resolve) => {
        setTimeout(() => {
          resolve(mockData.orderDetail)
        }, 300)
      })
    }
    return request({
      url: `/order/detail/${id}`,
      method: 'get'
    })
  },

  createPayOrder: (data) => {
    if (USE_MOCK) {
      return new Promise((resolve) => {
        setTimeout(() => {
          resolve({
            code: 200,
            data: {
              form: '<form action="https://openapi-sandbox.dl.alipaydev.com/gateway.do" method="post"><input type="hidden" name="alipay_sdk" value="alipay-easysdk" /><input type="hidden" name="biz_content" value="{\\"out_trade_no\\":\\"' + data.orderId + '\\",\\"product_code\\":\\"FAST_INSTANT_TRADE_PAY\\",\\"subject\\":\\"' + data.subject + '\\",\\"total_amount\\":\\"' + data.amount + '\\"}" /><input type="hidden" name="charset" value="UTF-8" /><input type="hidden" name="format" value="JSON" /><input type="hidden" name="method" value="alipay.trade.page.pay" /><input type="hidden" name="notify_url" value="http://localhost:54870/api/order/pay/alipay/callback" /><input type="hidden" name="return_url" value="http://localhost:54870/api/order/pay/alipay/return" /><input type="hidden" name="sign" value="mock-sign" /><input type="hidden" name="sign_type" value="RSA2" /><input type="hidden" name="timestamp" value="2026-04-09 12:00:00" /><input type="hidden" name="version" value="1.0" /><input type="submit" value="立即支付" style="display:none" /></form><script>document.forms[0].submit();</script>',
              orderId: data.orderId,
              status: 'SUCCESS'
            },
            message: '创建支付订单成功'
          })
        }, 500)
      })
    }
    return request({
      url: '/order/pay/alipay/create',
      method: 'post',
      data
    })
  },

  queryPayStatus: (orderId) => {
    if (USE_MOCK) {
      return new Promise((resolve) => {
        setTimeout(() => {
          resolve({
            code: 200,
            data: {
              orderId: orderId,
              status: 'SUCCESS'
            },
            message: '查询支付状态成功'
          })
        }, 300)
      })
    }
    return request({
      url: `/order/pay/alipay/query/${orderId}`,
      method: 'get'
    })
  },

  getCustomerUserInfo: () => {
    if (USE_MOCK) {
      return new Promise((resolve) => {
        setTimeout(() => {
          resolve({
            code: 200,
            data: {
              userId: 1,
              username: 'test03',
              mobile: '13800138000',
              avatar: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=professional%20user%20avatar%20portrait&image_size=square',
              emailAddr: 'test@example.com'
            }
          })
        }, 300)
      })
    }
    return request({
      url: '/auth/customer/userinfo',
      method: 'get'
    })
  },

  cancelOrder: (orderId) => {
    if (USE_MOCK) {
      return new Promise((resolve) => {
        setTimeout(() => {
          resolve({
            code: 200,
            data: true,
            message: '取消订单成功'
          })
        }, 300)
      })
    }
    return request({
      url: `/order/cancel/${orderId}`,
      method: 'post'
    })
  },

  updateCustomerUserInfo: (data) => {
    if (USE_MOCK) {
      return new Promise((resolve) => {
        setTimeout(() => {
          resolve({
            code: 200,
            data: {
              userId: 1,
              username: data.username,
              mobile: '13800138000',
              avatar: data.avatar || '',
              emailAddr: data.emailAddr
            },
            message: '更新成功'
          })
        }, 300)
      })
    }
    return request({
      url: '/auth/customer/userinfo',
      method: 'put',
      data
    })
  },

  uploadAvatar: (file) => {
    if (USE_MOCK) {
      return new Promise((resolve) => {
        setTimeout(() => {
          resolve({
            code: 200,
            data: 'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&h=200&fit=crop',
            message: '上传成功'
          })
        }, 500)
      })
    }
    const formData = new FormData()
    formData.append('file', file)
    return request({
      url: '/common/oss/files/upload',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  updateCustomerUserAvatar: (avatarUrl) => {
    if (USE_MOCK) {
      return new Promise((resolve) => {
        setTimeout(() => {
          resolve({
            code: 200,
            data: {
              userId: 1,
              username: 'test03',
              mobile: '13800138000',
              avatar: avatarUrl,
              emailAddr: 'test@example.com'
            },
            message: '头像更新成功'
          })
        }, 300)
      })
    }
    return request({
      url: '/auth/customer/avatar',
      method: 'put',
      params: { avatarUrl }
    })
  }
}
