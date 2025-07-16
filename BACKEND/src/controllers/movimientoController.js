const express = require('express');
const router = express.Router();
const movimientoService = require('../services/movimientoService');

// GET /movimientos
// GET /movimientos?fecha=2025-07-16
router.get('/', async (req, res) => {
    try {
        const movimientos = await movimientoService.getAllMovimientos();

        if (movimientos && movimientos.length > 0) {
            res.status(200).json(movimientos);
        } else {
            res.status(404).json({ message: 'No se encontraron movimientos.' });
        }
    } catch (error) {
        res.status(500).json({ message: 'Hubo un error al obtener los movimientos.', error: error.message });
    }
});

module.exports = router;
