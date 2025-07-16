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

    getColaboradoresByIdInventario(InventarioId){
        return inventarioRepository.getColaboradoresByIdInventario(InventarioId);
    }

    createInventario(data){
        return inventarioRepository.create(data);
    }

    async crearInventarioOWNR(data){
        const inventario = await inventarioRepository.crearInventarioOWNR(data);

    await movimientoService.create({
        idUsuario: data.userID,
        nombreUsuario: data.nombreUsuario,
        idInventario: inventario.idInventario,
        descripcionInventario: inventario.descripcionInventario || data.descripcion,
        idElemento: null,
        descripcionElemento: null,
        tipoObjeto: 'Inventario',
        accion: 'CREÓ',
        descripcion: `Inventario creado: ${data.descripcion}`
    });

    return inventario;
    }

    async updateInventario(id, data){
        const updated = await inventarioRepository.update(id, data);

    await movimientoService.create({
        idUsuario: data.userID,
        nombreUsuario: data.nombreUsuario,
        idInventario: id,
        descripcionInventario: data.descripcionInventario,
        idElemento: null,
        descripcionElemento: null,
        tipoObjeto: 'Inventario',
        accion: 'EDITÓ',
        descripcion: `Inventario actualizado: ${data.descripcionInventario}`
    });

    return updated;
    }

    deleteInventario(id){
        return inventarioRepository.delete(id);
    }
}
module.exports = new InventarioService();