CREATE TABLE ostot (
    id integer NOT NULL,
    kiekko_id integer NOT NULL,
    myyja integer NOT NULL,
    ostaja integer NOT NULL,
    status integer NOT NULL DEFAULT 0
);

CREATE SEQUENCE ostot_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE ostot_id_seq OWNED BY ostot.id;
ALTER TABLE ONLY ostot ALTER COLUMN id SET DEFAULT nextval('ostot_id_seq'::regclass);
SELECT pg_catalog.setval('ostot_id_seq', 1, false);

ALTER TABLE ONLY ostot
    ADD CONSTRAINT ostot_pkey PRIMARY KEY (id);
ALTER TABLE ONLY ostot
    ADD CONSTRAINT ostot_ibfk_1 FOREIGN KEY (myyja) REFERENCES members(id) ON UPDATE CASCADE;
ALTER TABLE ONLY ostot
    ADD CONSTRAINT ostot_ibfk_2 FOREIGN KEY (ostaja) REFERENCES members(id) ON UPDATE CASCADE;
ALTER TABLE ONLY ostot
    ADD CONSTRAINT ostot_ibfk_3 FOREIGN KEY (kiekko_id) REFERENCES kiekot(id) ON UPDATE CASCADE;
