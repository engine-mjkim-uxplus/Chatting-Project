CREATE TABLE MJ.PRIVATE_ROOM
(
  ROOMNUM  NUMBER,
  NAME1    VARCHAR2(50 BYTE)                    NOT NULL,
  NAME2    VARCHAR2(50 BYTE)                    NOT NULL
)
TABLESPACE USERS
PCTUSED    0
PCTFREE    10
INITRANS   1
MAXTRANS   255
STORAGE    (
            INITIAL          64K
            NEXT             1M
            MINEXTENTS       1
            MAXEXTENTS       UNLIMITED
            PCTINCREASE      0
            BUFFER_POOL      DEFAULT
           )
LOGGING 
NOCOMPRESS 
NOCACHE
NOPARALLEL
MONITORING;


--  There is no statement for index MJ.SYS_C0011277.
--  The object is created when the parent object is created.

ALTER TABLE MJ.PRIVATE_ROOM ADD (
  PRIMARY KEY
  (ROOMNUM)
  USING INDEX
    TABLESPACE USERS
    PCTFREE    10
    INITRANS   2
    MAXTRANS   255
    STORAGE    (
                INITIAL          64K
                NEXT             1M
                MINEXTENTS       1
                MAXEXTENTS       UNLIMITED
                PCTINCREASE      0
                BUFFER_POOL      DEFAULT
               )
  ENABLE VALIDATE);
