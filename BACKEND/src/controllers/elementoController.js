const express = require('express');
const elementoService = require('../services/elemetnoService');
const router = express.Router();

router.get('/', async (req, res) => {
    try {
        const elementos = await elementoService.getAllElementos();
        res.json(elementos);
    } catch (error) {
        res.status(500).json({ message: 'Hubo un error al obtener los Elemento', error: error.message });
    }
});

router.get('/:id', async (req, res) => {
    try {
        const elemento = await elementoService.getElementoById(req.params.id);
        if (elemento) {
            res.status(200).json(elemento);
        } else {
            res.status(404).json({ message: 'Elemento no encontrado' });
        }
    } catch (error) {
        res.status(500).json({ message: 'Hubo un error al obtener el Elemento', error: error.message });
    }
});

router.post('/', async (req, res) => {
    try {
        const newElemento = await elementoService.createElemento(req.body);
        if (newElemento) {
            res.status(201).json(newElemento);
        } else {
            res.status(404).json({ message: 'No se pudo registrar el Elemento' });
        }
    } catch (error) {
        res.status(500).json({ message: 'Hubo un error al crear el Elemento', error: error.message });
    }
});

router.put('/:id', async (req, res) => {
    try {
        const updateElemento = await elementoService.updateElemento(req.params.id, req.body);
        if (updateElemento) {
            res.status(200).json(updateElemento);
        } else {
            res.status(404).json({ message: 'Elemento no actualizada' });
        }
    } catch (error) {
        res.status(500).json({ message: 'Hubo un error al actualizar el Elemento', error: error.message });
    }
});

router.delete('/:id', async (req, res) => {
    try {
        const deleteElemento = await elementoService.deleteElemento(req.params.id);
        if (deleteElemento) {
            res.status(200).send();
        } else {
            res.status(400).json({ message: 'Elemento no eliminada' });
        }
    } catch (error) {
        res.status(500).json({ message: 'Hubo un error al eliminar el Elemento', error: error.message });
    }
});

module.exports = router;