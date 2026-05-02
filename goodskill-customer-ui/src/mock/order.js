import Mock from 'mockjs'

const mockData = [
  {
    url: '/api/order/customer/list',
    method: 'get',
    response: (req) => {
      const pageSize = req.query.pageSize || 10
      const pageNum = req.query.pageNum || 1
      const total = 15
      
      const items = []
      for (let i = 0; i < pageSize; i++) {
        const id = (pageNum - 1) * pageSize + i + 1
        const state = i % 3 + 1
        const stateDesc = state === 1 ? '待支付' : state === 2 ? '已支付' : '已取消'
        items.push({
          id: Mock.Random.id(),
          seckillId: id,
          userPhone: '17612185982',
          state: state,
          status: state,
          stateDesc: stateDesc,
          createTime: new Date(Date.now() - i * 24 * 60 * 60 * 1000).toISOString(),
          updateTime: new Date(Date.now() - i * 24 * 60 * 60 * 1000).toISOString(),
          payCompleteTime: state >= 2 ? new Date(Date.now() - i * 24 * 60 * 60 * 1000 + 30 * 60 * 1000).toISOString() : null,
          alipayTradeNo: state >= 2 ? Mock.Random.id() : null,
          seckillPrice: Mock.Random.float(5, 50, 2, 2),
          goodsName: `商品${id}`,
          goodsTitle: `商品${id} - 限时秒杀`,
          goodsImg: `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=product%20photo%20${id}&image_size=square`
        })
      }
      
      return {
        code: 200,
        message: 'success',
        data: {
          records: items,
          total: total,
          size: pageSize,
          current: pageNum,
          pages: Math.ceil(total / pageSize)
        }
      }
    }
  },
  {
    url: '/api/order/customer/info/:id',
    method: 'get',
    response: (req) => {
      const id = req.params.id
      return {
        code: 200,
        message: 'success',
        data: {
          id: id,
          seckillId: 1,
          userPhone: '17612185982',
          state: 1,
          status: 1,
          stateDesc: '待支付',
          createTime: new Date().toISOString(),
          payCompleteTime: null,
          alipayTradeNo: null,
          seckillPrice: 29.99,
          goodsName: '商品1',
          goodsTitle: '商品1 - 限时秒杀',
          goodsImg: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=product%20photo%201&image_size=square'
        }
      }
    }
  },
  {
    url: '/api/order/customer/cancel/:id',
    method: 'post',
    response: () => {
      return {
        code: 0,
        msg: null,
        data: null
      }
    }
  },
  {
    url: '/api/order/pay/create',
    method: 'post',
    response: () => {
      return {
        code: 0,
        msg: null,
        data: {
          form: '<form action="https://example.com/pay" method="post"><input type="hidden" name="orderId" value="test_order_id"><button type="submit">前往支付</button></form>'
        }
      }
    }
  },
  {
    url: '/api/order/pay/query/:id',
    method: 'get',
    response: () => {
      return {
        code: 0,
        msg: null,
        data: {
          status: 'WAITING'
        }
      }
    }
  }
]

export default mockData