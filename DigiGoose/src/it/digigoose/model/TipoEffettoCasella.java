package it.digigoose.model;

public enum TipoEffettoCasella {
    NESSUNO,
    BALZO_E_RILANCIA,
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
    ATTENDI_DADO_SPECIFICO, // Richiede valori specifici dei dadi (3, 6) o (4, 5)
    VOLA_E_FERMA           // Effetto combinato: vola a una casella e fermati un turno
}