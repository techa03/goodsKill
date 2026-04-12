import request from '../utils/request'

const USE_MOCK = import.meta.env.VITE_USE_MOCK === 'true'

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
    data: [
      {
        id: 1001,
        goodsId: 1001,
        goodsName: 'iPhone 15 Pro',
        goodsTitle: '苹果最新款手机',
        seckillPrice: 7999,
        goodsPrice: 9999,
        stockCount: 100,
        endTime: new Date(Date.now() + 3600000).toISOString()
      },
      {
        id: 1002,
        goodsId: 1002,
        goodsName: 'MacBook Pro',
        goodsTitle: '苹果最新款笔记本电脑',
        seckillPrice: 12999,
        goodsPrice: 15999,
        stockCount: 50,
        endTime: new Date(Date.now() + 7200000).toISOString()
      },
      {
        id: 1003,
        goodsId: 1003,
        goodsName: 'AirPods Pro',
        goodsTitle: '苹果无线降噪耳机',
        seckillPrice: 1599,
        goodsPrice: 1999,
        stockCount: 200,
        endTime: new Date(Date.now() + 1800000).toISOString()
      }
    ]
  },
  orderList: {
    code: 200,
    data: [
      {
        id: 'ORD001',
        goodsName: 'iPhone 15 Pro',
        goodsImg: 'https://via.placeholder.com/200',
        seckillPrice: 7999,
        state: 1,
        stateDesc: '待支付',
        createTime: new Date().toISOString(),
        userPhone: '13800138000'
      },
      {
        id: 'ORD002',
        goodsName: 'MacBook Pro',
        goodsImg: 'https://via.placeholder.com/200',
        seckillPrice: 12999,
        state: 2,
        stateDesc: '已完成',
        createTime: new Date(Date.now() - 86400000).toISOString(),
        userPhone: '13800138000'
      }
    ]
  },
  orderDetail: {
    code: 200,
    data: {
      id: 'ORD001',
      goodsName: 'iPhone 15 Pro',
      goodsTitle: '苹果最新款手机 256GB 深空灰',
      goodsImg: 'https://via.placeholder.com/200',
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
          resolve(mockData.goodsList.data.find(item => item.id === Number(id)))
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
              avatar: '',
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
  }
}
