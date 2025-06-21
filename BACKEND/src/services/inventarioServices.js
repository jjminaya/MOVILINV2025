const inventarioRepository = require('../repositories/inventarioRepository');

class InventarioService{
    getAllInventarios(){
        return inventarioRepository.findAll();
    }

    getInventarioById(id){
        return inventarioRepository.findById(id);
    }

    getInventariosByUserID(UserId){
        return inventarioRepository.getInventariosByUserID(UserId);
    }

    createInventario(data){
        return inventarioRepository.create(data);
    }

    updateInventario(id, data){
        return inventarioRepository.update(id, data);
    }

    deleteInventario(id){
        return inventarioRepository.delete(id);
    }
}
module.exports = new InventarioService();