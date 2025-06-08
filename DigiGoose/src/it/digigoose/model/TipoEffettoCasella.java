package it.digigoose.model;

/**
 * rappresenta la lista di tutti gli effetti delle caselle speciali
 */

public enum TipoEffettoCasella {
    NESSUNO,
    RILANCIA,
    PONTE,
    RITORNO_INIZIO,
    RADDOPPIA_MOVIMENTO,
    FERMA_UN_TURNO,
    FERMA_DUE_TURNI,
    VOLA_AVANTI,
    ATTENDI_DADO,
    RILANCIA_TORNA_INDIETRO,
    RADDOPPIA_PUNTEGGIO,
    VAI_INDIETRO,
    LABIRINTO,
    PRIGIONE,
    POZZO,
    ATTENDI_DADO_SPECIFICO, 
    VOLA_E_FERMA           
}