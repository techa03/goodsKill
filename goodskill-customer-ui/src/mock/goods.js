import Mock from 'mockjs'

const mockData = [
  {
    url: '/api/seckill/customer/list',
    method: 'get',
    response: (req) => {
      const pageSize = req.query.pageSize || 10
      const pageNum = req.query.pageNum || 1
      const total = 20
      
      const items = []
      for (let i = 0; i < pageSize; i++) {
        const id = (pageNum - 1) * pageSize + i + 1
        items.push({
          id: id,
          seckillId: id,
          goodsId: id,
          name: `商品${id}`,
          goodsName: `商品${id}`,
          goodsTitle: `商品${id} - 限时秒杀`,
          price: Mock.Random.float(10, 100, 2, 2),
          seckillPrice: Mock.Random.float(5, 50, 2, 2),
          number: Mock.Random.integer(10, 100),
          stockCount: Mock.Random.integer(10, 100),
          startTime: new Date().toISOString(),
          endTime: new Date(Date.now() + 24 * 60 * 60 * 1000).toISOString(),
          photoUrl: `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=product%20photo%20${id}&image_size=square`,
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
    url: '/api/seckill/customer/info/:id',
    method: 'get',
    response: (req) => {
      const id = req.params.id
      return {
        code: 200,
        message: 'success',
        data: {
          id: id,
          seckillId: id,
          goodsId: id,
          name: `商品${id}`,
          goodsName: `商品${id}`,
          goodsTitle: `商品${id} - 限时秒杀`,
          price: Mock.Random.float(10, 100, 2, 2),
          seckillPrice: Mock.Random.float(5, 50, 2, 2),
          number: Mock.Random.integer(10, 100),
          stockCount: Mock.Random.integer(10, 100),
          startTime: new Date().toISOString(),
          endTime: new Date(Date.now() + 24 * 60 * 60 * 1000).toISOString(),
          photoUrl: `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=product%20photo%20${id}&image_size=square`,
          goodsImg: `https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=product%20photo%20${id}&image_size=square`
        }
      }
    }
  },
  {
    url: '/api/seckill/expose/:id',
    method: 'get',
    response: () => {
      return {
        code: 0,
        msg: null,
        data: {
          seckillId: 1,
          exposed: true,
          md5: 'test_md5'
        }
      }
    }
  },
  {
    url: '/api/seckill/execute',
    method: 'post',
    response: () => {
      return {
        code: 0,
        msg: null,
        data: {
          orderId: Mock.Random.id(),
          seckillId: 1,
          userPhone: '17612185982',
          state: 1,
          createTime: new Date().toISOString()
        }
      }
    }
  }
]

export default mockData