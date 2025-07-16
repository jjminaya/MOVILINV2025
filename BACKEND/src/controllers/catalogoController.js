const express = require('express');
const catalogoService = require('../services/catalogoService');
const router = express.Router();

router.get('/', async (req, res) => {
    try {
        const catalogos = await catalogoService.getAllCatalogos();
        res.json(catalogos);
    } catch (error) {
        res.status(500).json({ message: 'Hubo un error al obtener los catálogos', error: error.message });
    }
});

router.get('/:id', async (req, res) => {
    try {
        const catalogo = await catalogoService.getCatalogoById(req.params.id);
        if (catalogo) {
            res.json(catalogo);
        } else {
            res.status(404).json({ message: 'Catálogo no encontrado' });
        }
    } catch (error) {
        res.status(500).json({ message: 'Hubo un error al obtener el catálogo', error: error.message });
    }
});

router.post('/', async (req, res) => {
    try {
        const newCatalogo = await catalogoService.createCatalogo(req.body);
        res.status(201).json(newCatalogo);
    } catch (error) {
        res.status(500).json({ message: 'Hubo un error al crear el catálogo', error: error.message });
    }
});

router.put('/:id', async (req, res) => {
    try {
        const updatedCatalogo = await catalogoService.updateCatalogo(req.params.id, req.body);
        res.json(updatedCatalogo);
    } catch (error) {
        res.status(500).json({ message: 'Hubo un error al actualizar el catálogo', error: error.message });
    }
});

router.delete('/:id', async (req, res) => {
    try {
        const deleted = await catalogoService.deleteCatalogo(req.params.id);
        if (deleted) {
            res.json({ message: 'Catálogo eliminado correctamente' });
        } else {
            res.status(404).json({ message: 'Catálogo no encontrado' });
        }
    } catch (error) {
        res.status(500).json({ message: 'Hubo un error al eliminar el catálogo', error: error.message });
    }
});

module.exports = router;
