const elementoRepository = require('../repositories/elementoRespository');

class ElementoService{
    getAllElementos(){
        return elementoRepository.findAll();
    }

    getElementoById(id){
        return elementoRepository.findById(id);
    }

    createElemento(data){
        return elementoRepository.create(data);
    }

    updateElemento(id, data){
        return elementoRepository.update(id, data);
    }

    deleteElemento(id){
        return elementoRepository.delete(id);
    }
}
module.exports = new ElementoService();