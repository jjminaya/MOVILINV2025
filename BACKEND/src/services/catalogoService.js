const catalogoRepository = require('../repositories/catalogoRepository');

class CatalogoService {
    getAllCatalogos() {
        return catalogoRepository.findAll();
    }

    getCatalogoById(id) {
        return catalogoRepository.findById(id);
    }

    createCatalogo(data) {
        return catalogoRepository.create(data);
    }

    updateCatalogo(id, data) {
        return catalogoRepository.update(id, data);
    }

    deleteCatalogo(id) {
        return catalogoRepository.delete(id);
    }
}

module.exports = new CatalogoService();
