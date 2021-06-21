# Konkurrens Beadandó

Fordításhoz és futtatáshoz használd a ```make``` és ```make run``` parancsokat.

Az vendégek egy sin és cos függvényekből álló formula segítségével reprezentálom, mellyel tökéletesen lehet imitálni a csúcsidőket.

Zárási időben ezt a formulát egy 10-es szorzóval bővítem, hiszen zárási időn kívül azért mégiscsak kevesebben próbálkoznak fodrászhoz menni.

Az időt nem én nullázom, hanem felhasználom a java.util.Date osztályt és azt a modulo segítségével fordítom le az álltalunk definiált napi időintervallumra (new Date().getTime() % 9600).
