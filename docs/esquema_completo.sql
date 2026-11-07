--
-- PostgreSQL database dump
--

\restrict sp9WQwwLKhxK4yw9l3diaWEwH0WQnvCFcerK6adg0GqRkQiHkwqxtEB6QkCpxrS

-- Dumped from database version 18.4 (Debian 18.4-1+b1)
-- Dumped by pg_dump version 18.4 (Debian 18.4-1+b1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: TBD deporte; Type: SCHEMA; Schema: -; Owner: postgres
--

CREATE SCHEMA "TBD deporte";


ALTER SCHEMA "TBD deporte" OWNER TO postgres;

--
-- Name: buscar_usuario(character varying, character varying); Type: FUNCTION; Schema: TBD deporte; Owner: postgres
--

CREATE FUNCTION "TBD deporte".buscar_usuario(p_nombre character varying, p_password character varying) RETURNS TABLE(id_usern integer, nombre character varying, password character varying, activo boolean)
    LANGUAGE sql
    AS $$
    select u.id_userN, u.nombre, u.password, u.activo
    from userN u
    where u.nombre = p_nombre and u.password = p_password;
$$;


ALTER FUNCTION "TBD deporte".buscar_usuario(p_nombre character varying, p_password character varying) OWNER TO postgres;

--
-- Name: crear_partido(integer, date, character varying, character varying, integer, integer); Type: FUNCTION; Schema: TBD deporte; Owner: postgres
--

CREATE FUNCTION "TBD deporte".crear_partido(p_deporte_id integer, p_fecha_inicio date, p_tipo character varying, p_cantidad_vs character varying, p_local integer, p_visitante integer) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
DECLARE v_id INT;
BEGIN
    IF p_local = p_visitante THEN
        RAISE EXCEPTION 'El local y el visitante no pueden ser iguales';
    END IF;
    IF p_tipo = 'EQUIPO' AND
       (SELECT count(*) FROM equipo
        WHERE equipo_id IN (p_local, p_visitante) AND deporte_id = p_deporte_id) <> 2 THEN
        RAISE EXCEPTION 'Ambos equipos deben ser del deporte del torneo';
    END IF;
    INSERT INTO partido (torneo_deporte_id, torneo_fecha_inicio, tipo_partido, cantidad_vs)
    VALUES (p_deporte_id, p_fecha_inicio, p_tipo, p_cantidad_vs)
    RETURNING partido_id INTO v_id;
    IF p_tipo = '1VS1' THEN
        INSERT INTO partido_1vs1 (partido_id, jugador_local_id, jugador_visitante_id)
        VALUES (v_id, p_local, p_visitante);
    ELSE
        INSERT INTO partido_equipo_vs_equipo (partido_id, equipo_local_id, equipo_visitante_id)
        VALUES (v_id, p_local, p_visitante);
    END IF;
    RETURN TRUE;
END;
$$;


ALTER FUNCTION "TBD deporte".crear_partido(p_deporte_id integer, p_fecha_inicio date, p_tipo character varying, p_cantidad_vs character varying, p_local integer, p_visitante integer) OWNER TO postgres;

--
-- Name: crear_torneo(integer, character varying, date, date); Type: FUNCTION; Schema: TBD deporte; Owner: postgres
--

CREATE FUNCTION "TBD deporte".crear_torneo(p_deporte_id integer, p_nombre character varying, p_inicio date, p_fin date) RETURNS boolean
    LANGUAGE sql
    AS $$
    INSERT INTO torneo (deporte_id, nombre, fecha_inicio, fecha_fin)
    VALUES (p_deporte_id, p_nombre, p_inicio, p_fin);
    SELECT TRUE;
$$;


ALTER FUNCTION "TBD deporte".crear_torneo(p_deporte_id integer, p_nombre character varying, p_inicio date, p_fin date) OWNER TO postgres;

--
-- Name: credenciales_usern(integer); Type: FUNCTION; Schema: TBD deporte; Owner: postgres
--

CREATE FUNCTION "TBD deporte".credenciales_usern(p_id_usern integer) RETURNS TABLE(id_iu integer)
    LANGUAGE sql
    AS $$
    select funcion_iu.id_iu
    from (
        select rol_funcion.id_funcion
        from (
            select userN_rol.id_rol
            from (
                select userN.id_userN
                from userN
                where userN.id_userN = p_id_userN
            ) uno, userN_rol
            where uno.id_userN = userN_rol.id_userN
        ) dos, rol_funcion
        where dos.id_rol = rol_funcion.id_rol
    ) tres, funcion_iu
    where tres.id_funcion = funcion_iu.id_funcion;
$$;


ALTER FUNCTION "TBD deporte".credenciales_usern(p_id_usern integer) OWNER TO postgres;

--
-- Name: interfaces_usuario(integer); Type: FUNCTION; Schema: TBD deporte; Owner: postgres
--

CREATE FUNCTION "TBD deporte".interfaces_usuario(p_id_usern integer) RETURNS TABLE(id_iu integer, nombre character varying, descripcion character varying)
    LANGUAGE sql
    AS $$
    SELECT iu.id_iu,
           iu.nombre,
           iu.descripcion
    FROM (
        SELECT id_iu
        FROM credenciales_userN(p_id_userN)
    ) credenciales, iu
    WHERE credenciales.id_iu = iu.id_iu
    ORDER BY iu.id_iu;
$$;


ALTER FUNCTION "TBD deporte".interfaces_usuario(p_id_usern integer) OWNER TO postgres;

--
-- Name: listar_deportes(); Type: FUNCTION; Schema: TBD deporte; Owner: postgres
--

CREATE FUNCTION "TBD deporte".listar_deportes() RETURNS TABLE(deporte_id integer, nombre character varying)
    LANGUAGE sql
    AS $$
    SELECT d.deporte_id, d.nombre
    FROM deporte d
    ORDER BY d.deporte_id;
$$;


ALTER FUNCTION "TBD deporte".listar_deportes() OWNER TO postgres;

--
-- Name: listar_equipos(); Type: FUNCTION; Schema: TBD deporte; Owner: postgres
--

CREATE FUNCTION "TBD deporte".listar_equipos() RETURNS TABLE(equipo_id integer, nombre character varying, deporte_id integer)
    LANGUAGE sql
    AS $$
    SELECT
        e.equipo_id,
        e.nombre,
        e.deporte_id
    FROM equipo e
    ORDER BY e.equipo_id;
$$;


ALTER FUNCTION "TBD deporte".listar_equipos() OWNER TO postgres;

--
-- Name: listar_equipos_detalle(); Type: FUNCTION; Schema: TBD deporte; Owner: postgres
--

CREATE FUNCTION "TBD deporte".listar_equipos_detalle() RETURNS TABLE(equipo_id integer, equipo character varying, deporte character varying)
    LANGUAGE sql
    AS $$
    SELECT e.equipo_id, e.nombre, d.nombre
    FROM (
        SELECT e.equipo_id, e.nombre, e.deporte_id
        FROM equipo e
    ) e, deporte d
    WHERE e.deporte_id = d.deporte_id
    ORDER BY e.equipo_id;
$$;


ALTER FUNCTION "TBD deporte".listar_equipos_detalle() OWNER TO postgres;

--
-- Name: listar_jugadores(); Type: FUNCTION; Schema: TBD deporte; Owner: postgres
--

CREATE FUNCTION "TBD deporte".listar_jugadores() RETURNS TABLE(persona_id integer, nombre character varying, email character varying, contacto character varying)
    LANGUAGE sql
    AS $$
    SELECT p.persona_id, p.nombre, p.email, p.contacto
    FROM (
        SELECT j.persona_id
        FROM jugador j
    ) j, persona p
    WHERE j.persona_id = p.persona_id
    ORDER BY p.persona_id;
$$;


ALTER FUNCTION "TBD deporte".listar_jugadores() OWNER TO postgres;

--
-- Name: listar_partidos(); Type: FUNCTION; Schema: TBD deporte; Owner: postgres
--

CREATE FUNCTION "TBD deporte".listar_partidos() RETURNS TABLE(partido_id integer, torneo character varying, tipo character varying, cantidad_vs character varying, local_nombre character varying, visitante_nombre character varying, fecha date, hora time without time zone, arbitro character varying, arbitro_id integer)
    LANGUAGE sql
    AS $$
    SELECT
        p.partido_id,
        p.nombre                                                    AS torneo,
        p.tipo_partido                                              AS tipo,
        p.cantidad_vs,
        COALESCE(
            (SELECT el.nombre
             FROM equipo el
             WHERE el.equipo_id = (
                 SELECT b.equipo_local_id
                 FROM partido_equipo_vs_equipo b
                 WHERE b.partido_id = p.partido_id
             )),
            (SELECT pl.nombre
             FROM persona pl
             WHERE pl.persona_id = (
                 SELECT a.jugador_local_id
                 FROM partido_1vs1 a
                 WHERE a.partido_id = p.partido_id
             ))
        )                                                           AS local_nombre,
        COALESCE(
            (SELECT ev.nombre
             FROM equipo ev
             WHERE ev.equipo_id = (
                 SELECT b.equipo_visitante_id
                 FROM partido_equipo_vs_equipo b
                 WHERE b.partido_id = p.partido_id
             )),
            (SELECT pv.nombre
             FROM persona pv
             WHERE pv.persona_id = (
                 SELECT a.jugador_visitante_id
                 FROM partido_1vs1 a
                 WHERE a.partido_id = p.partido_id
             ))
        )                                                           AS visitante_nombre,
        (SELECT x.fecha
         FROM programacion x
         WHERE x.partido_id = p.partido_id
         ORDER BY x.fecha_registro DESC
         LIMIT 1)                                                   AS fecha,
        (SELECT x.hora
         FROM programacion x
         WHERE x.partido_id = p.partido_id
         ORDER BY x.fecha_registro DESC
         LIMIT 1)                                                   AS hora,
        (SELECT pa.nombre
         FROM persona pa
         WHERE pa.persona_id = (
             SELECT x.arbitro_id
             FROM programacion x
             WHERE x.partido_id = p.partido_id
             ORDER BY x.fecha_registro DESC
             LIMIT 1
         ))                                                         AS arbitro,
        (SELECT x.arbitro_id
         FROM programacion x
         WHERE x.partido_id = p.partido_id
         ORDER BY x.fecha_registro DESC
         LIMIT 1)                                                   AS arbitro_id
    FROM (
        -- Aquí se aplica la segunda igualdad (fecha_inicio)
        SELECT uno.partido_id,
               uno.tipo_partido,
               uno.cantidad_vs,
               uno.nombre,
               uno.torneo_fecha_inicio
        FROM (
            -- Aquí se aplica la primera igualdad (deporte_id)
            SELECT partido.partido_id,
                   partido.tipo_partido,
                   partido.cantidad_vs,
                   partido.torneo_fecha_inicio,
                   torneo.nombre,
                   torneo.fecha_inicio
            FROM (
                SELECT partido_id,
                       torneo_deporte_id,
                       torneo_fecha_inicio,
                       tipo_partido,
                       cantidad_vs
                FROM partido
            ) partido,
            (
                SELECT deporte_id,
                       fecha_inicio,
                       nombre
                FROM torneo
            ) torneo
            WHERE partido.torneo_deporte_id = torneo.deporte_id
        ) uno
        WHERE uno.torneo_fecha_inicio = uno.fecha_inicio
    ) p
    ORDER BY fecha, hora;
$$;


ALTER FUNCTION "TBD deporte".listar_partidos() OWNER TO postgres;

--
-- Name: listar_roles_funciones(); Type: FUNCTION; Schema: TBD deporte; Owner: postgres
--

CREATE FUNCTION "TBD deporte".listar_roles_funciones() RETURNS TABLE(rol_nombre character varying, funcion_nombre character varying, funcion_descripcion character varying)
    LANGUAGE sql
    AS $$
    select uno.nombre_rol, funcion.nombre, funcion.descripcion
    from (
        select rol.id_rol, rol.nombre as nombre_rol, rol_funcion.id_funcion
        from rol, rol_funcion
        where rol.id_rol = rol_funcion.id_rol
    ) uno, funcion
    where uno.id_funcion = funcion.id_funcion
    order by uno.id_rol, funcion.id_funcion;
$$;


ALTER FUNCTION "TBD deporte".listar_roles_funciones() OWNER TO postgres;

--
-- Name: listar_torneos(); Type: FUNCTION; Schema: TBD deporte; Owner: postgres
--

CREATE FUNCTION "TBD deporte".listar_torneos() RETURNS TABLE(deporte_id integer, deporte character varying, nombre character varying, fecha_inicio date, fecha_fin date)
    LANGUAGE sql
    AS $$
    SELECT t.deporte_id, d.nombre, t.nombre, t.fecha_inicio, t.fecha_fin
    FROM (
        SELECT t.deporte_id, t.nombre, t.fecha_inicio, t.fecha_fin
        FROM torneo t
    ) t, deporte d
    WHERE t.deporte_id = d.deporte_id
    ORDER BY t.fecha_inicio DESC;
$$;


ALTER FUNCTION "TBD deporte".listar_torneos() OWNER TO postgres;

--
-- Name: partidos_arbitro(integer); Type: FUNCTION; Schema: TBD deporte; Owner: postgres
--

CREATE FUNCTION "TBD deporte".partidos_arbitro(p_id_usern integer) RETURNS TABLE(partido_id integer, torneo_nombre character varying, tipo_partido character varying, cantidad_vs character varying, equipo_local character varying, equipo_visitante character varying, fecha date, hora time without time zone, arbitro_nombre character varying)
    LANGUAGE sql
    AS $$
    SELECT 
        nueve.partido_id, 
        nueve.torneo_nombre, 
        nueve.tipo_partido, 
        nueve.cantidad_vs, 
        nueve.equipo_local, 
        e2.nombre AS equipo_visitante, 
        nueve.fecha, 
        nueve.hora, 
        nueve.arbitro_nombre
    FROM (
        SELECT 
            ocho.partido_id, 
            ocho.torneo_nombre, 
            ocho.tipo_partido, 
            ocho.cantidad_vs, 
            e1.nombre AS equipo_local, 
            ocho.equipo_visitante_id AS ev, 
            ocho.fecha, 
            ocho.hora, 
            ocho.arbitro_nombre
        FROM (
            SELECT 
                peve.partido_id, 
                siete.torneo_nombre, 
                siete.tipo_partido, 
                siete.cantidad_vs,
                peve.equipo_local_id AS el, 
                peve.equipo_visitante_id, 
                siete.fecha, 
                siete.hora, 
                siete.arbitro_nombre
            FROM (
                SELECT  
                    cinco.partido_id, 
                    torneo.nombre AS torneo_nombre, 
                    cinco.tipo_partido, 
                    cinco.cantidad_vs, 
                    cinco.fecha, 
                    cinco.hora, 
                    cinco.nombre AS arbitro_nombre
                FROM (
                    SELECT 
                        partido.partido_id, 
                        partido.torneo_deporte_id,
                        partido.tipo_partido, 
                        partido.cantidad_vs, 
                        cuatro.fecha, 
                        cuatro.hora, 
                        cuatro.nombre
                    FROM (
                        SELECT 
                            programacion.partido_id, 
                            programacion.fecha, 
                            programacion.hora, 
                            tres.nombre
                        FROM (
                            SELECT 
                                arbitro.persona_id, 
                                dos.nombre
                            FROM (
                                SELECT 
                                    persona.persona_id, 
                                    persona.nombre
                                FROM (
                                    SELECT persona_id
                                    FROM userN
                                    WHERE userN.id_userN = p_id_userN
                                ) uno, persona
                                WHERE uno.persona_id = persona.persona_id
                            ) dos, arbitro
                            WHERE arbitro.persona_id = dos.persona_id
                        ) tres, programacion
                        WHERE tres.persona_id = programacion.arbitro_id
                    ) cuatro, partido
                    WHERE partido.partido_id = cuatro.partido_id
                ) cinco, torneo
                WHERE torneo.deporte_id = cinco.torneo_deporte_id
            ) siete, partido_equipo_vs_equipo peve
            WHERE peve.partido_id = siete.partido_id
        ) ocho, equipo e1
        WHERE e1.equipo_id = ocho.el
    ) nueve, equipo e2
    WHERE e2.equipo_id = nueve.ev
    ORDER BY nueve.fecha, nueve.hora;
$$;


ALTER FUNCTION "TBD deporte".partidos_arbitro(p_id_usern integer) OWNER TO postgres;

--
-- Name: partidos_arbitro_1vs1(integer); Type: FUNCTION; Schema: TBD deporte; Owner: postgres
--

CREATE FUNCTION "TBD deporte".partidos_arbitro_1vs1(p_id_usern integer) RETURNS TABLE(partido_id integer, torneo_nombre character varying, tipo_partido character varying, cantidad_vs character varying, jugador_local character varying, jugador_visitante character varying, fecha date, hora time without time zone, arbitro_nombre character varying)
    LANGUAGE sql
    AS $$
    SELECT 
        ocho.partido_id, 
        ocho.torneo_nombre, 
        ocho.tipo_partido, 
        ocho.cantidad_vs,
        ocho.nombre AS jugador_local, 
        p.nombre AS jugador_visitante, 
        ocho.fecha, 
        ocho.hora, 
        ocho.arbitro_nombre
    FROM (
        SELECT  
            siete.partido_id, 
            siete.torneo_nombre, 
            siete.tipo_partido, 
            siete.cantidad_vs,
            p.nombre, 
            siete.jugador_visitante_id, 
            siete.fecha, 
            siete.hora, 
            siete.arbitro_nombre
        FROM (
            SELECT 
                p1.partido_id, 
                seis.torneo_nombre, 
                seis.tipo_partido, 
                seis.cantidad_vs,
                p1.jugador_local_id, 
                p1.jugador_visitante_id, 
                seis.fecha, 
                seis.hora, 
                seis.arbitro_nombre
            FROM (
                SELECT  
                    cinco.partido_id, 
                    torneo.nombre AS torneo_nombre, 
                    cinco.tipo_partido, 
                    cinco.cantidad_vs, 
                    cinco.fecha, 
                    cinco.hora, 
                    cinco.nombre AS arbitro_nombre
                FROM (
                    SELECT 
                        partido.partido_id, 
                        partido.torneo_deporte_id,
                        partido.tipo_partido, 
                        partido.cantidad_vs, 
                        cuatro.fecha, 
                        cuatro.hora, 
                        cuatro.nombre
                    FROM (
                        SELECT 
                            programacion.partido_id, 
                            programacion.fecha, 
                            programacion.hora, 
                            tres.nombre
                        FROM (
                            SELECT 
                                arbitro.persona_id, 
                                dos.nombre
                            FROM (
                                SELECT 
                                    persona.persona_id, 
                                    persona.nombre
                                FROM (
                                    SELECT persona_id
                                    FROM userN
                                    WHERE userN.id_userN = p_id_userN
                                ) uno, persona
                                WHERE uno.persona_id = persona.persona_id
                            ) dos, arbitro
                            WHERE arbitro.persona_id = dos.persona_id
                        ) tres, programacion
                        WHERE tres.persona_id = programacion.arbitro_id
                    ) cuatro, partido
                    WHERE partido.partido_id = cuatro.partido_id
                ) cinco, torneo
                WHERE torneo.deporte_id = cinco.torneo_deporte_id
            ) seis, partido_1vs1 p1
            WHERE p1.partido_id = seis.partido_id
        ) siete, persona p
        WHERE p.persona_id = siete.jugador_local_id
    ) ocho, persona p
    WHERE p.persona_id = ocho.jugador_visitante_id
    ORDER BY ocho.fecha, ocho.hora;
$$;


ALTER FUNCTION "TBD deporte".partidos_arbitro_1vs1(p_id_usern integer) OWNER TO postgres;

--
-- Name: partidos_arbitro_todos(integer); Type: FUNCTION; Schema: TBD deporte; Owner: postgres
--

CREATE FUNCTION "TBD deporte".partidos_arbitro_todos(p_id_usern integer) RETURNS TABLE(partido_id integer, torneo_nombre character varying, tipo_partido character varying, cantidad_vs character varying, local_nombre character varying, visitante_nombre character varying, fecha date, hora time without time zone, arbitro_nombre character varying)
    LANGUAGE sql
    AS $$
    -- Partidos de equipo
    SELECT 
        partido_id, torneo_nombre, tipo_partido, cantidad_vs,
        equipo_local, equipo_visitante, fecha, hora, arbitro_nombre
    FROM partidos_arbitro(p_id_userN)

    UNION ALL

    -- Partidos 1VS1
    SELECT 
        partido_id, torneo_nombre, tipo_partido, cantidad_vs,
        jugador_local, jugador_visitante, fecha, hora, arbitro_nombre
    FROM partidos_arbitro_1vs1(p_id_userN)

    ORDER BY fecha, hora;
$$;


ALTER FUNCTION "TBD deporte".partidos_arbitro_todos(p_id_usern integer) OWNER TO postgres;

--
-- Name: partidos_jugador_1vs1(integer); Type: FUNCTION; Schema: TBD deporte; Owner: postgres
--

CREATE FUNCTION "TBD deporte".partidos_jugador_1vs1(p_id_usern integer) RETURNS TABLE(partido_id integer, torneo_nombre character varying, tipo_partido character varying, cantidad_vs character varying, jugador_local character varying, jugador_visitante character varying, fecha date, hora time without time zone, arbitro_nombre character varying)
    LANGUAGE sql
    AS $$
    SELECT 
        ocho.partido_id,
        ocho.torneo_nombre,
        ocho.tipo_partido,
        ocho.cantidad_vs,
        ocho.jugador_local,
        ocho.jugador_visitante,
        ocho.fecha,
        ocho.hora,
        ocho.arbitro_nombre
    FROM (
        SELECT 
            siete.partido_id,
            siete.torneo_nombre,
            siete.tipo_partido,
            siete.cantidad_vs,
            pl.nombre AS jugador_local,
            pv.nombre AS jugador_visitante,
            siete.jugador_local_id,
            siete.jugador_visitante_id,
            siete.fecha,
            siete.hora,
            siete.arbitro_nombre
        FROM (
            SELECT 
                p1.partido_id,
                seis.torneo_nombre,
                seis.tipo_partido,
                seis.cantidad_vs,
                p1.jugador_local_id,
                p1.jugador_visitante_id,
                seis.fecha,
                seis.hora,
                seis.arbitro_nombre
            FROM (
                SELECT 
                    cinco.partido_id,
                    torneo.nombre AS torneo_nombre,
                    cinco.tipo_partido,
                    cinco.cantidad_vs,
                    cinco.fecha,
                    cinco.hora,
                    cinco.arbitro_nombre
                FROM (
                    SELECT 
                        partido.partido_id,
                        partido.torneo_deporte_id,
                        partido.tipo_partido,
                        partido.cantidad_vs,
                        cuatro.fecha,
                        cuatro.hora,
                        cuatro.arbitro_nombre
                    FROM (
                        SELECT 
                            programacion.partido_id,
                            programacion.fecha,
                            programacion.hora,
                            per_arb.nombre AS arbitro_nombre
                        FROM programacion, persona per_arb
                        WHERE programacion.arbitro_id = per_arb.persona_id
                    ) cuatro, partido
                    WHERE partido.partido_id = cuatro.partido_id
                      AND partido.tipo_partido = '1VS1'
                ) cinco, torneo
                WHERE torneo.deporte_id = cinco.torneo_deporte_id
            ) seis, partido_1vs1 p1
            WHERE p1.partido_id = seis.partido_id
        ) siete, persona pl, persona pv
        WHERE pl.persona_id = siete.jugador_local_id
          AND pv.persona_id = siete.jugador_visitante_id
    ) ocho,
    (
        SELECT persona_id
        FROM userN
        WHERE id_userN = p_id_userN
    ) u
    WHERE u.persona_id = ocho.jugador_local_id
       OR u.persona_id = ocho.jugador_visitante_id
    ORDER BY ocho.fecha, ocho.hora;
$$;


ALTER FUNCTION "TBD deporte".partidos_jugador_1vs1(p_id_usern integer) OWNER TO postgres;

--
-- Name: registrar_equipo(character varying, integer); Type: FUNCTION; Schema: TBD deporte; Owner: postgres
--

CREATE FUNCTION "TBD deporte".registrar_equipo(p_nombre character varying, p_deporte_id integer) RETURNS boolean
    LANGUAGE sql
    AS $$
    INSERT INTO equipo (nombre, deporte_id)
    VALUES (p_nombre, p_deporte_id);

    SELECT TRUE;
$$;


ALTER FUNCTION "TBD deporte".registrar_equipo(p_nombre character varying, p_deporte_id integer) OWNER TO postgres;

--
-- Name: registrar_jugador(character varying, character varying, character varying); Type: FUNCTION; Schema: TBD deporte; Owner: postgres
--

CREATE FUNCTION "TBD deporte".registrar_jugador(p_nombre character varying, p_email character varying, p_contacto character varying) RETURNS boolean
    LANGUAGE plpgsql
    AS $$
DECLARE v_id INT;
BEGIN
    INSERT INTO persona (nombre, email, contacto)
    VALUES (p_nombre, p_email, p_contacto)
    RETURNING persona_id INTO v_id;
    INSERT INTO jugador (persona_id, partidos_jugados) VALUES (v_id, 0);
    RETURN TRUE;
END;
$$;


ALTER FUNCTION "TBD deporte".registrar_jugador(p_nombre character varying, p_email character varying, p_contacto character varying) OWNER TO postgres;

--
-- Name: registrar_log(); Type: FUNCTION; Schema: TBD deporte; Owner: postgres
--

CREATE FUNCTION "TBD deporte".registrar_log() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
DECLARE
    usuario_actual VARCHAR(100);
BEGIN

    usuario_actual :=
        COALESCE(
            NULLIF(current_setting('app.user_name', true), ''),
            current_user
        );

    IF TG_OP = 'INSERT' THEN

        INSERT INTO log (
            tabla,
            operacion,
            userNombre,
            datoNuevo,
            datoViejo
        )
        VALUES (
            TG_TABLE_NAME,
            TG_OP,
            usuario_actual,
            ROW(NEW)::TEXT,
            NULL
        );

        RETURN NEW;

    ELSIF TG_OP = 'UPDATE' THEN

        INSERT INTO log (
            tabla,
            operacion,
            userNombre,
            datoNuevo,
            datoViejo
        )
        VALUES (
            TG_TABLE_NAME,
            TG_OP,
            usuario_actual,
            ROW(NEW)::TEXT,
            ROW(OLD)::TEXT
        );

        RETURN NEW;

    ELSIF TG_OP = 'DELETE' THEN

        INSERT INTO log (
            tabla,
            operacion,
            userNombre,
            datoNuevo,
            datoViejo
        )
        VALUES (
            TG_TABLE_NAME,
            TG_OP,
            usuario_actual,
            NULL,
            ROW(OLD)::TEXT
        );
        
        RETURN OLD;

    END IF;

    RETURN NULL;

END;
$$;


ALTER FUNCTION "TBD deporte".registrar_log() OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: arbitro; Type: TABLE; Schema: TBD deporte; Owner: postgres
--

CREATE TABLE "TBD deporte".arbitro (
    persona_id integer NOT NULL
);


ALTER TABLE "TBD deporte".arbitro OWNER TO postgres;

--
-- Name: deporte; Type: TABLE; Schema: TBD deporte; Owner: postgres
--

CREATE TABLE "TBD deporte".deporte (
    deporte_id integer NOT NULL,
    nombre character varying(100) NOT NULL
);


ALTER TABLE "TBD deporte".deporte OWNER TO postgres;

--
-- Name: deporte_deporte_id_seq; Type: SEQUENCE; Schema: TBD deporte; Owner: postgres
--

CREATE SEQUENCE "TBD deporte".deporte_deporte_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "TBD deporte".deporte_deporte_id_seq OWNER TO postgres;

--
-- Name: deporte_deporte_id_seq; Type: SEQUENCE OWNED BY; Schema: TBD deporte; Owner: postgres
--

ALTER SEQUENCE "TBD deporte".deporte_deporte_id_seq OWNED BY "TBD deporte".deporte.deporte_id;


--
-- Name: equipo; Type: TABLE; Schema: TBD deporte; Owner: postgres
--

CREATE TABLE "TBD deporte".equipo (
    equipo_id integer NOT NULL,
    nombre character varying(150) NOT NULL,
    deporte_id integer NOT NULL
);


ALTER TABLE "TBD deporte".equipo OWNER TO postgres;

--
-- Name: equipo_equipo_id_seq; Type: SEQUENCE; Schema: TBD deporte; Owner: postgres
--

CREATE SEQUENCE "TBD deporte".equipo_equipo_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "TBD deporte".equipo_equipo_id_seq OWNER TO postgres;

--
-- Name: equipo_equipo_id_seq; Type: SEQUENCE OWNED BY; Schema: TBD deporte; Owner: postgres
--

ALTER SEQUENCE "TBD deporte".equipo_equipo_id_seq OWNED BY "TBD deporte".equipo.equipo_id;


--
-- Name: funcion; Type: TABLE; Schema: TBD deporte; Owner: postgres
--

CREATE TABLE "TBD deporte".funcion (
    id_funcion integer NOT NULL,
    nombre character varying(100) NOT NULL,
    descripcion character varying(250),
    categoria character varying(50)
);


ALTER TABLE "TBD deporte".funcion OWNER TO postgres;

--
-- Name: funcion_id_funcion_seq; Type: SEQUENCE; Schema: TBD deporte; Owner: postgres
--

CREATE SEQUENCE "TBD deporte".funcion_id_funcion_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "TBD deporte".funcion_id_funcion_seq OWNER TO postgres;

--
-- Name: funcion_id_funcion_seq; Type: SEQUENCE OWNED BY; Schema: TBD deporte; Owner: postgres
--

ALTER SEQUENCE "TBD deporte".funcion_id_funcion_seq OWNED BY "TBD deporte".funcion.id_funcion;


--
-- Name: funcion_iu; Type: TABLE; Schema: TBD deporte; Owner: postgres
--

CREATE TABLE "TBD deporte".funcion_iu (
    id_funcion integer NOT NULL,
    id_iu integer NOT NULL
);


ALTER TABLE "TBD deporte".funcion_iu OWNER TO postgres;

--
-- Name: iu; Type: TABLE; Schema: TBD deporte; Owner: postgres
--

CREATE TABLE "TBD deporte".iu (
    id_iu integer NOT NULL,
    nombre character varying(100) NOT NULL,
    descripcion character varying(250)
);


ALTER TABLE "TBD deporte".iu OWNER TO postgres;

--
-- Name: iu_id_iu_seq; Type: SEQUENCE; Schema: TBD deporte; Owner: postgres
--

CREATE SEQUENCE "TBD deporte".iu_id_iu_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "TBD deporte".iu_id_iu_seq OWNER TO postgres;

--
-- Name: iu_id_iu_seq; Type: SEQUENCE OWNED BY; Schema: TBD deporte; Owner: postgres
--

ALTER SEQUENCE "TBD deporte".iu_id_iu_seq OWNED BY "TBD deporte".iu.id_iu;


--
-- Name: jugador; Type: TABLE; Schema: TBD deporte; Owner: postgres
--

CREATE TABLE "TBD deporte".jugador (
    persona_id integer NOT NULL,
    partidos_jugados integer
);


ALTER TABLE "TBD deporte".jugador OWNER TO postgres;

--
-- Name: jugador_equipo; Type: TABLE; Schema: TBD deporte; Owner: postgres
--

CREATE TABLE "TBD deporte".jugador_equipo (
    jugador_id integer NOT NULL,
    equipo_id integer NOT NULL,
    fecha_ingreso date DEFAULT CURRENT_DATE
);


ALTER TABLE "TBD deporte".jugador_equipo OWNER TO postgres;

--
-- Name: log; Type: TABLE; Schema: TBD deporte; Owner: postgres
--

CREATE TABLE "TBD deporte".log (
    id_log integer NOT NULL,
    fecha date DEFAULT CURRENT_DATE NOT NULL,
    hora time without time zone DEFAULT CURRENT_TIME NOT NULL,
    tabla character varying(100) NOT NULL,
    operacion character varying(20) NOT NULL,
    usernombre character varying(100),
    datonuevo character varying(5000),
    datoviejo character varying(5000)
);


ALTER TABLE "TBD deporte".log OWNER TO postgres;

--
-- Name: log_id_log_seq; Type: SEQUENCE; Schema: TBD deporte; Owner: postgres
--

CREATE SEQUENCE "TBD deporte".log_id_log_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "TBD deporte".log_id_log_seq OWNER TO postgres;

--
-- Name: log_id_log_seq; Type: SEQUENCE OWNED BY; Schema: TBD deporte; Owner: postgres
--

ALTER SEQUENCE "TBD deporte".log_id_log_seq OWNED BY "TBD deporte".log.id_log;


--
-- Name: lugar; Type: TABLE; Schema: TBD deporte; Owner: postgres
--

CREATE TABLE "TBD deporte".lugar (
    lugar_id integer NOT NULL,
    nombre character varying(150) NOT NULL,
    direccion character varying(250)
);


ALTER TABLE "TBD deporte".lugar OWNER TO postgres;

--
-- Name: lugar_lugar_id_seq; Type: SEQUENCE; Schema: TBD deporte; Owner: postgres
--

CREATE SEQUENCE "TBD deporte".lugar_lugar_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "TBD deporte".lugar_lugar_id_seq OWNER TO postgres;

--
-- Name: lugar_lugar_id_seq; Type: SEQUENCE OWNED BY; Schema: TBD deporte; Owner: postgres
--

ALTER SEQUENCE "TBD deporte".lugar_lugar_id_seq OWNED BY "TBD deporte".lugar.lugar_id;


--
-- Name: partido; Type: TABLE; Schema: TBD deporte; Owner: postgres
--

CREATE TABLE "TBD deporte".partido (
    partido_id integer NOT NULL,
    torneo_deporte_id integer NOT NULL,
    torneo_fecha_inicio date NOT NULL,
    tipo_partido character varying(20) NOT NULL,
    cantidad_vs character varying(50) NOT NULL,
    CONSTRAINT partido_tipo_partido_check CHECK (((tipo_partido)::text = ANY ((ARRAY['1VS1'::character varying, 'EQUIPO'::character varying])::text[])))
);


ALTER TABLE "TBD deporte".partido OWNER TO postgres;

--
-- Name: partido_1vs1; Type: TABLE; Schema: TBD deporte; Owner: postgres
--

CREATE TABLE "TBD deporte".partido_1vs1 (
    partido_id integer NOT NULL,
    jugador_local_id integer NOT NULL,
    jugador_visitante_id integer NOT NULL,
    CONSTRAINT partido_1vs1_check CHECK ((jugador_local_id <> jugador_visitante_id))
);


ALTER TABLE "TBD deporte".partido_1vs1 OWNER TO postgres;

--
-- Name: partido_equipo_vs_equipo; Type: TABLE; Schema: TBD deporte; Owner: postgres
--

CREATE TABLE "TBD deporte".partido_equipo_vs_equipo (
    partido_id integer NOT NULL,
    equipo_local_id integer NOT NULL,
    equipo_visitante_id integer NOT NULL,
    CONSTRAINT partido_equipo_vs_equipo_check CHECK ((equipo_local_id <> equipo_visitante_id))
);


ALTER TABLE "TBD deporte".partido_equipo_vs_equipo OWNER TO postgres;

--
-- Name: partido_partido_id_seq; Type: SEQUENCE; Schema: TBD deporte; Owner: postgres
--

CREATE SEQUENCE "TBD deporte".partido_partido_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "TBD deporte".partido_partido_id_seq OWNER TO postgres;

--
-- Name: partido_partido_id_seq; Type: SEQUENCE OWNED BY; Schema: TBD deporte; Owner: postgres
--

ALTER SEQUENCE "TBD deporte".partido_partido_id_seq OWNED BY "TBD deporte".partido.partido_id;


--
-- Name: persona; Type: TABLE; Schema: TBD deporte; Owner: postgres
--

CREATE TABLE "TBD deporte".persona (
    persona_id integer NOT NULL,
    nombre character varying(150) NOT NULL,
    email character varying(150),
    contacto character varying(150)
);


ALTER TABLE "TBD deporte".persona OWNER TO postgres;

--
-- Name: persona_persona_id_seq; Type: SEQUENCE; Schema: TBD deporte; Owner: postgres
--

CREATE SEQUENCE "TBD deporte".persona_persona_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "TBD deporte".persona_persona_id_seq OWNER TO postgres;

--
-- Name: persona_persona_id_seq; Type: SEQUENCE OWNED BY; Schema: TBD deporte; Owner: postgres
--

ALTER SEQUENCE "TBD deporte".persona_persona_id_seq OWNED BY "TBD deporte".persona.persona_id;


--
-- Name: programacion; Type: TABLE; Schema: TBD deporte; Owner: postgres
--

CREATE TABLE "TBD deporte".programacion (
    partido_id integer NOT NULL,
    lugar_id integer,
    arbitro_id integer,
    fecha date NOT NULL,
    hora time without time zone NOT NULL,
    fecha_registro timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE "TBD deporte".programacion OWNER TO postgres;

--
-- Name: puntaje_resultado; Type: TABLE; Schema: TBD deporte; Owner: postgres
--

CREATE TABLE "TBD deporte".puntaje_resultado (
    partido_id integer NOT NULL,
    lado character varying(10) NOT NULL,
    puntos integer DEFAULT 0 NOT NULL,
    CONSTRAINT puntaje_resultado_lado_check CHECK (((lado)::text = ANY ((ARRAY['LOCAL'::character varying, 'VISITANTE'::character varying])::text[])))
);


ALTER TABLE "TBD deporte".puntaje_resultado OWNER TO postgres;

--
-- Name: resultado_partido; Type: TABLE; Schema: TBD deporte; Owner: postgres
--

CREATE TABLE "TBD deporte".resultado_partido (
    partido_id integer NOT NULL,
    empate boolean DEFAULT false NOT NULL,
    observaciones character varying(250),
    fecha_registro timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE "TBD deporte".resultado_partido OWNER TO postgres;

--
-- Name: rol; Type: TABLE; Schema: TBD deporte; Owner: postgres
--

CREATE TABLE "TBD deporte".rol (
    id_rol integer NOT NULL,
    nombre character varying(100) NOT NULL,
    descripcion character varying(250)
);


ALTER TABLE "TBD deporte".rol OWNER TO postgres;

--
-- Name: rol_funcion; Type: TABLE; Schema: TBD deporte; Owner: postgres
--

CREATE TABLE "TBD deporte".rol_funcion (
    id_rol integer NOT NULL,
    id_funcion integer NOT NULL
);


ALTER TABLE "TBD deporte".rol_funcion OWNER TO postgres;

--
-- Name: rol_id_rol_seq; Type: SEQUENCE; Schema: TBD deporte; Owner: postgres
--

CREATE SEQUENCE "TBD deporte".rol_id_rol_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "TBD deporte".rol_id_rol_seq OWNER TO postgres;

--
-- Name: rol_id_rol_seq; Type: SEQUENCE OWNED BY; Schema: TBD deporte; Owner: postgres
--

ALTER SEQUENCE "TBD deporte".rol_id_rol_seq OWNED BY "TBD deporte".rol.id_rol;


--
-- Name: sesion; Type: TABLE; Schema: TBD deporte; Owner: postgres
--

CREATE TABLE "TBD deporte".sesion (
    id_sesion integer NOT NULL,
    id_usern integer NOT NULL,
    fecha date DEFAULT CURRENT_DATE NOT NULL,
    hora time without time zone DEFAULT CURRENT_TIME NOT NULL,
    activo boolean DEFAULT true NOT NULL,
    pid integer
);


ALTER TABLE "TBD deporte".sesion OWNER TO postgres;

--
-- Name: sesion_id_sesion_seq; Type: SEQUENCE; Schema: TBD deporte; Owner: postgres
--

CREATE SEQUENCE "TBD deporte".sesion_id_sesion_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "TBD deporte".sesion_id_sesion_seq OWNER TO postgres;

--
-- Name: sesion_id_sesion_seq; Type: SEQUENCE OWNED BY; Schema: TBD deporte; Owner: postgres
--

ALTER SEQUENCE "TBD deporte".sesion_id_sesion_seq OWNED BY "TBD deporte".sesion.id_sesion;


--
-- Name: torneo; Type: TABLE; Schema: TBD deporte; Owner: postgres
--

CREATE TABLE "TBD deporte".torneo (
    deporte_id integer NOT NULL,
    nombre character varying(150) NOT NULL,
    fecha_inicio date NOT NULL,
    fecha_fin date,
    CONSTRAINT torneo_check CHECK (((fecha_fin IS NULL) OR (fecha_fin >= fecha_inicio)))
);


ALTER TABLE "TBD deporte".torneo OWNER TO postgres;

--
-- Name: usern; Type: TABLE; Schema: TBD deporte; Owner: postgres
--

CREATE TABLE "TBD deporte".usern (
    id_usern integer NOT NULL,
    nombre character varying(100) NOT NULL,
    password character varying(255) NOT NULL,
    activo boolean DEFAULT true NOT NULL,
    persona_id integer
);


ALTER TABLE "TBD deporte".usern OWNER TO postgres;

--
-- Name: usern_id_usern_seq; Type: SEQUENCE; Schema: TBD deporte; Owner: postgres
--

CREATE SEQUENCE "TBD deporte".usern_id_usern_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE "TBD deporte".usern_id_usern_seq OWNER TO postgres;

--
-- Name: usern_id_usern_seq; Type: SEQUENCE OWNED BY; Schema: TBD deporte; Owner: postgres
--

ALTER SEQUENCE "TBD deporte".usern_id_usern_seq OWNED BY "TBD deporte".usern.id_usern;


--
-- Name: usern_rol; Type: TABLE; Schema: TBD deporte; Owner: postgres
--

CREATE TABLE "TBD deporte".usern_rol (
    id_usern integer NOT NULL,
    id_rol integer NOT NULL,
    desde date DEFAULT CURRENT_DATE NOT NULL,
    hasta date,
    activo boolean DEFAULT true NOT NULL,
    CONSTRAINT usern_rol_check CHECK (((hasta IS NULL) OR (hasta >= desde)))
);


ALTER TABLE "TBD deporte".usern_rol OWNER TO postgres;

--
-- Name: deporte deporte_id; Type: DEFAULT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".deporte ALTER COLUMN deporte_id SET DEFAULT nextval('"TBD deporte".deporte_deporte_id_seq'::regclass);


--
-- Name: equipo equipo_id; Type: DEFAULT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".equipo ALTER COLUMN equipo_id SET DEFAULT nextval('"TBD deporte".equipo_equipo_id_seq'::regclass);


--
-- Name: funcion id_funcion; Type: DEFAULT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".funcion ALTER COLUMN id_funcion SET DEFAULT nextval('"TBD deporte".funcion_id_funcion_seq'::regclass);


--
-- Name: iu id_iu; Type: DEFAULT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".iu ALTER COLUMN id_iu SET DEFAULT nextval('"TBD deporte".iu_id_iu_seq'::regclass);


--
-- Name: log id_log; Type: DEFAULT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".log ALTER COLUMN id_log SET DEFAULT nextval('"TBD deporte".log_id_log_seq'::regclass);


--
-- Name: lugar lugar_id; Type: DEFAULT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".lugar ALTER COLUMN lugar_id SET DEFAULT nextval('"TBD deporte".lugar_lugar_id_seq'::regclass);


--
-- Name: partido partido_id; Type: DEFAULT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".partido ALTER COLUMN partido_id SET DEFAULT nextval('"TBD deporte".partido_partido_id_seq'::regclass);


--
-- Name: persona persona_id; Type: DEFAULT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".persona ALTER COLUMN persona_id SET DEFAULT nextval('"TBD deporte".persona_persona_id_seq'::regclass);


--
-- Name: rol id_rol; Type: DEFAULT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".rol ALTER COLUMN id_rol SET DEFAULT nextval('"TBD deporte".rol_id_rol_seq'::regclass);


--
-- Name: sesion id_sesion; Type: DEFAULT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".sesion ALTER COLUMN id_sesion SET DEFAULT nextval('"TBD deporte".sesion_id_sesion_seq'::regclass);


--
-- Name: usern id_usern; Type: DEFAULT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".usern ALTER COLUMN id_usern SET DEFAULT nextval('"TBD deporte".usern_id_usern_seq'::regclass);


--
-- Name: arbitro arbitro_pkey; Type: CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".arbitro
    ADD CONSTRAINT arbitro_pkey PRIMARY KEY (persona_id);


--
-- Name: deporte deporte_nombre_key; Type: CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".deporte
    ADD CONSTRAINT deporte_nombre_key UNIQUE (nombre);


--
-- Name: deporte deporte_pkey; Type: CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".deporte
    ADD CONSTRAINT deporte_pkey PRIMARY KEY (deporte_id);


--
-- Name: equipo equipo_pkey; Type: CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".equipo
    ADD CONSTRAINT equipo_pkey PRIMARY KEY (equipo_id);


--
-- Name: funcion_iu funcion_iu_pkey; Type: CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".funcion_iu
    ADD CONSTRAINT funcion_iu_pkey PRIMARY KEY (id_funcion, id_iu);


--
-- Name: funcion funcion_nombre_key; Type: CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".funcion
    ADD CONSTRAINT funcion_nombre_key UNIQUE (nombre);


--
-- Name: funcion funcion_pkey; Type: CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".funcion
    ADD CONSTRAINT funcion_pkey PRIMARY KEY (id_funcion);


--
-- Name: iu iu_nombre_key; Type: CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".iu
    ADD CONSTRAINT iu_nombre_key UNIQUE (nombre);


--
-- Name: iu iu_pkey; Type: CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".iu
    ADD CONSTRAINT iu_pkey PRIMARY KEY (id_iu);


--
-- Name: jugador_equipo jugador_equipo_pkey; Type: CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".jugador_equipo
    ADD CONSTRAINT jugador_equipo_pkey PRIMARY KEY (jugador_id, equipo_id);


--
-- Name: jugador jugador_pkey; Type: CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".jugador
    ADD CONSTRAINT jugador_pkey PRIMARY KEY (persona_id);


--
-- Name: log log_pkey; Type: CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".log
    ADD CONSTRAINT log_pkey PRIMARY KEY (id_log);


--
-- Name: lugar lugar_pkey; Type: CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".lugar
    ADD CONSTRAINT lugar_pkey PRIMARY KEY (lugar_id);


--
-- Name: partido_1vs1 partido_1vs1_pkey; Type: CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".partido_1vs1
    ADD CONSTRAINT partido_1vs1_pkey PRIMARY KEY (partido_id);


--
-- Name: partido_equipo_vs_equipo partido_equipo_vs_equipo_pkey; Type: CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".partido_equipo_vs_equipo
    ADD CONSTRAINT partido_equipo_vs_equipo_pkey PRIMARY KEY (partido_id);


--
-- Name: partido partido_pkey; Type: CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".partido
    ADD CONSTRAINT partido_pkey PRIMARY KEY (partido_id);


--
-- Name: persona persona_pkey; Type: CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".persona
    ADD CONSTRAINT persona_pkey PRIMARY KEY (persona_id);


--
-- Name: programacion programacion_pkey; Type: CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".programacion
    ADD CONSTRAINT programacion_pkey PRIMARY KEY (partido_id, fecha_registro);


--
-- Name: puntaje_resultado puntaje_resultado_pkey; Type: CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".puntaje_resultado
    ADD CONSTRAINT puntaje_resultado_pkey PRIMARY KEY (partido_id, lado);


--
-- Name: resultado_partido resultado_partido_pkey; Type: CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".resultado_partido
    ADD CONSTRAINT resultado_partido_pkey PRIMARY KEY (partido_id);


--
-- Name: rol_funcion rol_funcion_pkey; Type: CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".rol_funcion
    ADD CONSTRAINT rol_funcion_pkey PRIMARY KEY (id_rol, id_funcion);


--
-- Name: rol rol_nombre_key; Type: CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".rol
    ADD CONSTRAINT rol_nombre_key UNIQUE (nombre);


--
-- Name: rol rol_pkey; Type: CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".rol
    ADD CONSTRAINT rol_pkey PRIMARY KEY (id_rol);


--
-- Name: sesion sesion_pkey; Type: CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".sesion
    ADD CONSTRAINT sesion_pkey PRIMARY KEY (id_sesion, id_usern);


--
-- Name: torneo torneo_pkey; Type: CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".torneo
    ADD CONSTRAINT torneo_pkey PRIMARY KEY (deporte_id, fecha_inicio);


--
-- Name: usern usern_nombre_key; Type: CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".usern
    ADD CONSTRAINT usern_nombre_key UNIQUE (nombre);


--
-- Name: usern usern_pkey; Type: CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".usern
    ADD CONSTRAINT usern_pkey PRIMARY KEY (id_usern);


--
-- Name: usern_rol usern_rol_pkey; Type: CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".usern_rol
    ADD CONSTRAINT usern_rol_pkey PRIMARY KEY (id_usern, id_rol, desde);


--
-- Name: arbitro trg_log_arbitro; Type: TRIGGER; Schema: TBD deporte; Owner: postgres
--

CREATE TRIGGER trg_log_arbitro AFTER INSERT OR DELETE OR UPDATE ON "TBD deporte".arbitro FOR EACH ROW EXECUTE FUNCTION "TBD deporte".registrar_log();


--
-- Name: deporte trg_log_deporte; Type: TRIGGER; Schema: TBD deporte; Owner: postgres
--

CREATE TRIGGER trg_log_deporte AFTER INSERT OR DELETE OR UPDATE ON "TBD deporte".deporte FOR EACH ROW EXECUTE FUNCTION "TBD deporte".registrar_log();


--
-- Name: equipo trg_log_equipo; Type: TRIGGER; Schema: TBD deporte; Owner: postgres
--

CREATE TRIGGER trg_log_equipo AFTER INSERT OR DELETE OR UPDATE ON "TBD deporte".equipo FOR EACH ROW EXECUTE FUNCTION "TBD deporte".registrar_log();


--
-- Name: jugador trg_log_jugador; Type: TRIGGER; Schema: TBD deporte; Owner: postgres
--

CREATE TRIGGER trg_log_jugador AFTER INSERT OR DELETE OR UPDATE ON "TBD deporte".jugador FOR EACH ROW EXECUTE FUNCTION "TBD deporte".registrar_log();


--
-- Name: jugador_equipo trg_log_jugador_equipo; Type: TRIGGER; Schema: TBD deporte; Owner: postgres
--

CREATE TRIGGER trg_log_jugador_equipo AFTER INSERT OR DELETE OR UPDATE ON "TBD deporte".jugador_equipo FOR EACH ROW EXECUTE FUNCTION "TBD deporte".registrar_log();


--
-- Name: lugar trg_log_lugar; Type: TRIGGER; Schema: TBD deporte; Owner: postgres
--

CREATE TRIGGER trg_log_lugar AFTER INSERT OR DELETE OR UPDATE ON "TBD deporte".lugar FOR EACH ROW EXECUTE FUNCTION "TBD deporte".registrar_log();


--
-- Name: partido trg_log_partido; Type: TRIGGER; Schema: TBD deporte; Owner: postgres
--

CREATE TRIGGER trg_log_partido AFTER INSERT OR DELETE OR UPDATE ON "TBD deporte".partido FOR EACH ROW EXECUTE FUNCTION "TBD deporte".registrar_log();


--
-- Name: partido_1vs1 trg_log_partido_1vs1; Type: TRIGGER; Schema: TBD deporte; Owner: postgres
--

CREATE TRIGGER trg_log_partido_1vs1 AFTER INSERT OR DELETE OR UPDATE ON "TBD deporte".partido_1vs1 FOR EACH ROW EXECUTE FUNCTION "TBD deporte".registrar_log();


--
-- Name: partido_equipo_vs_equipo trg_log_partido_equipo_vs_equipo; Type: TRIGGER; Schema: TBD deporte; Owner: postgres
--

CREATE TRIGGER trg_log_partido_equipo_vs_equipo AFTER INSERT OR DELETE OR UPDATE ON "TBD deporte".partido_equipo_vs_equipo FOR EACH ROW EXECUTE FUNCTION "TBD deporte".registrar_log();


--
-- Name: persona trg_log_persona; Type: TRIGGER; Schema: TBD deporte; Owner: postgres
--

CREATE TRIGGER trg_log_persona AFTER INSERT OR DELETE OR UPDATE ON "TBD deporte".persona FOR EACH ROW EXECUTE FUNCTION "TBD deporte".registrar_log();


--
-- Name: programacion trg_log_programacion; Type: TRIGGER; Schema: TBD deporte; Owner: postgres
--

CREATE TRIGGER trg_log_programacion AFTER INSERT OR DELETE OR UPDATE ON "TBD deporte".programacion FOR EACH ROW EXECUTE FUNCTION "TBD deporte".registrar_log();


--
-- Name: puntaje_resultado trg_log_puntaje_resultado; Type: TRIGGER; Schema: TBD deporte; Owner: postgres
--

CREATE TRIGGER trg_log_puntaje_resultado AFTER INSERT OR DELETE OR UPDATE ON "TBD deporte".puntaje_resultado FOR EACH ROW EXECUTE FUNCTION "TBD deporte".registrar_log();


--
-- Name: resultado_partido trg_log_resultado_partido; Type: TRIGGER; Schema: TBD deporte; Owner: postgres
--

CREATE TRIGGER trg_log_resultado_partido AFTER INSERT OR DELETE OR UPDATE ON "TBD deporte".resultado_partido FOR EACH ROW EXECUTE FUNCTION "TBD deporte".registrar_log();


--
-- Name: torneo trg_log_torneo; Type: TRIGGER; Schema: TBD deporte; Owner: postgres
--

CREATE TRIGGER trg_log_torneo AFTER INSERT OR DELETE OR UPDATE ON "TBD deporte".torneo FOR EACH ROW EXECUTE FUNCTION "TBD deporte".registrar_log();


--
-- Name: arbitro arbitro_persona_id_fkey; Type: FK CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".arbitro
    ADD CONSTRAINT arbitro_persona_id_fkey FOREIGN KEY (persona_id) REFERENCES "TBD deporte".persona(persona_id) ON DELETE CASCADE;


--
-- Name: equipo equipo_deporte_id_fkey; Type: FK CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".equipo
    ADD CONSTRAINT equipo_deporte_id_fkey FOREIGN KEY (deporte_id) REFERENCES "TBD deporte".deporte(deporte_id);


--
-- Name: partido_1vs1 fk_partido_1vs1; Type: FK CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".partido_1vs1
    ADD CONSTRAINT fk_partido_1vs1 FOREIGN KEY (partido_id) REFERENCES "TBD deporte".partido(partido_id);


--
-- Name: partido_equipo_vs_equipo fk_partido_equipo_vs_equipo; Type: FK CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".partido_equipo_vs_equipo
    ADD CONSTRAINT fk_partido_equipo_vs_equipo FOREIGN KEY (partido_id) REFERENCES "TBD deporte".partido(partido_id);


--
-- Name: funcion_iu funcion_iu_id_funcion_fkey; Type: FK CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".funcion_iu
    ADD CONSTRAINT funcion_iu_id_funcion_fkey FOREIGN KEY (id_funcion) REFERENCES "TBD deporte".funcion(id_funcion);


--
-- Name: funcion_iu funcion_iu_id_iu_fkey; Type: FK CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".funcion_iu
    ADD CONSTRAINT funcion_iu_id_iu_fkey FOREIGN KEY (id_iu) REFERENCES "TBD deporte".iu(id_iu);


--
-- Name: jugador_equipo jugador_equipo_equipo_id_fkey; Type: FK CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".jugador_equipo
    ADD CONSTRAINT jugador_equipo_equipo_id_fkey FOREIGN KEY (equipo_id) REFERENCES "TBD deporte".equipo(equipo_id);


--
-- Name: jugador_equipo jugador_equipo_jugador_id_fkey; Type: FK CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".jugador_equipo
    ADD CONSTRAINT jugador_equipo_jugador_id_fkey FOREIGN KEY (jugador_id) REFERENCES "TBD deporte".jugador(persona_id);


--
-- Name: jugador jugador_persona_id_fkey; Type: FK CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".jugador
    ADD CONSTRAINT jugador_persona_id_fkey FOREIGN KEY (persona_id) REFERENCES "TBD deporte".persona(persona_id) ON DELETE CASCADE;


--
-- Name: partido_1vs1 partido_1vs1_jugador_local_id_fkey; Type: FK CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".partido_1vs1
    ADD CONSTRAINT partido_1vs1_jugador_local_id_fkey FOREIGN KEY (jugador_local_id) REFERENCES "TBD deporte".jugador(persona_id);


--
-- Name: partido_1vs1 partido_1vs1_jugador_visitante_id_fkey; Type: FK CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".partido_1vs1
    ADD CONSTRAINT partido_1vs1_jugador_visitante_id_fkey FOREIGN KEY (jugador_visitante_id) REFERENCES "TBD deporte".jugador(persona_id);


--
-- Name: partido_equipo_vs_equipo partido_equipo_vs_equipo_equipo_local_id_fkey; Type: FK CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".partido_equipo_vs_equipo
    ADD CONSTRAINT partido_equipo_vs_equipo_equipo_local_id_fkey FOREIGN KEY (equipo_local_id) REFERENCES "TBD deporte".equipo(equipo_id);


--
-- Name: partido_equipo_vs_equipo partido_equipo_vs_equipo_equipo_visitante_id_fkey; Type: FK CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".partido_equipo_vs_equipo
    ADD CONSTRAINT partido_equipo_vs_equipo_equipo_visitante_id_fkey FOREIGN KEY (equipo_visitante_id) REFERENCES "TBD deporte".equipo(equipo_id);


--
-- Name: partido partido_torneo_fkey; Type: FK CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".partido
    ADD CONSTRAINT partido_torneo_fkey FOREIGN KEY (torneo_deporte_id, torneo_fecha_inicio) REFERENCES "TBD deporte".torneo(deporte_id, fecha_inicio);


--
-- Name: programacion programacion_arbitro_id_fkey; Type: FK CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".programacion
    ADD CONSTRAINT programacion_arbitro_id_fkey FOREIGN KEY (arbitro_id) REFERENCES "TBD deporte".arbitro(persona_id);


--
-- Name: programacion programacion_lugar_id_fkey; Type: FK CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".programacion
    ADD CONSTRAINT programacion_lugar_id_fkey FOREIGN KEY (lugar_id) REFERENCES "TBD deporte".lugar(lugar_id);


--
-- Name: programacion programacion_partido_id_fkey; Type: FK CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".programacion
    ADD CONSTRAINT programacion_partido_id_fkey FOREIGN KEY (partido_id) REFERENCES "TBD deporte".partido(partido_id);


--
-- Name: puntaje_resultado puntaje_resultado_partido_id_fkey; Type: FK CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".puntaje_resultado
    ADD CONSTRAINT puntaje_resultado_partido_id_fkey FOREIGN KEY (partido_id) REFERENCES "TBD deporte".resultado_partido(partido_id);


--
-- Name: resultado_partido resultado_partido_partido_id_fkey; Type: FK CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".resultado_partido
    ADD CONSTRAINT resultado_partido_partido_id_fkey FOREIGN KEY (partido_id) REFERENCES "TBD deporte".partido(partido_id);


--
-- Name: rol_funcion rol_funcion_id_funcion_fkey; Type: FK CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".rol_funcion
    ADD CONSTRAINT rol_funcion_id_funcion_fkey FOREIGN KEY (id_funcion) REFERENCES "TBD deporte".funcion(id_funcion);


--
-- Name: rol_funcion rol_funcion_id_rol_fkey; Type: FK CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".rol_funcion
    ADD CONSTRAINT rol_funcion_id_rol_fkey FOREIGN KEY (id_rol) REFERENCES "TBD deporte".rol(id_rol);


--
-- Name: sesion sesion_id_usern_fkey; Type: FK CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".sesion
    ADD CONSTRAINT sesion_id_usern_fkey FOREIGN KEY (id_usern) REFERENCES "TBD deporte".usern(id_usern);


--
-- Name: torneo torneo_deporte_id_fkey; Type: FK CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".torneo
    ADD CONSTRAINT torneo_deporte_id_fkey FOREIGN KEY (deporte_id) REFERENCES "TBD deporte".deporte(deporte_id);


--
-- Name: usern usern_persona_id_fkey; Type: FK CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".usern
    ADD CONSTRAINT usern_persona_id_fkey FOREIGN KEY (persona_id) REFERENCES "TBD deporte".persona(persona_id);


--
-- Name: usern_rol usern_rol_id_rol_fkey; Type: FK CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".usern_rol
    ADD CONSTRAINT usern_rol_id_rol_fkey FOREIGN KEY (id_rol) REFERENCES "TBD deporte".rol(id_rol);


--
-- Name: usern_rol usern_rol_id_usern_fkey; Type: FK CONSTRAINT; Schema: TBD deporte; Owner: postgres
--

ALTER TABLE ONLY "TBD deporte".usern_rol
    ADD CONSTRAINT usern_rol_id_usern_fkey FOREIGN KEY (id_usern) REFERENCES "TBD deporte".usern(id_usern);


--
-- PostgreSQL database dump complete
--

\unrestrict sp9WQwwLKhxK4yw9l3diaWEwH0WQnvCFcerK6adg0GqRkQiHkwqxtEB6QkCpxrS

