const movimientoRepository = require('../repositories/movimientoRepository');

class MovimientoService {
    async create(data) {
        return movimientoRepository.create(data);
    }

    getAllMovimientos(){
        return movimientoRepository.findAll();
    }
}

module.exports = new MovimientoService();
