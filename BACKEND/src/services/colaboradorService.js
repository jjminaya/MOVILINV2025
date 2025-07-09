const colaboradorRepository = require('../repositories/colaboradorRepository');

class ColaboradorService{
    getAllColaboradores(){
        return colaboradorRepository.findAll();
    }

    getColaboradorById(id){
        return colaboradorRepository.findById(id);
    }

    createColaborador(data){
        return colaboradorRepository.create(data);
    }

    updateColaborador(id, data){
        return colaboradorRepository.update(id, data);
    }

    deleteColaborador(id){
        return colaboradorRepository.delete(id);
    }

    verificarUsuarioValido(username) {
        return colaboradorRepository.verificarUsuarioValido(username);
    }
}
module.exports = new ColaboradorService();