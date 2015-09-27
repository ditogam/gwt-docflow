create table tile_data as

SELECT 0 _layer, ppcityid _id,AsBinary( case when Within(buffered, the_geom)=1 then buffered else (Intersection(buffered,the_geom)) end) geom,'natural='||hex('water') _tags_name, z,x,y
FROM map_info,(select x1, y1,x2, y2,z,x,y, transform(BuildMbr(MbrMinX(mGeometry)-diff, MbrMinY(mGeometry)-diff,MbrMaxX(mGeometry)+ diff, MbrMaxY(mGeometry)+ diff, 3035),4326) buffered from (
select *,transform(Geometry,3035) mGeometry from
(select *,BuildMbr(x1, y1,x2, y2,4326) Geometry,BuildMbr(x1, y1,x2, y2,4326) Geometry from (
select  x1 , y1, x2 , y2, 50 diff, z,x,y from tiles_g
)))
) po 
where ppcityid in (
select pkid from idx_map_info_the_geom where pkid MATCH
RtreeIntersects(x1, y1,x2, y2))



union all
SELECT 1 _layer, id _id,AsBinary( case when Within(buffered, the_geom)=1 then buffered else (Intersection(buffered,the_geom)) end) geom,'natural='||hex('beach') _tags_name, z,x,y
FROM settlements,(select x1, y1,x2, y2,z,x,y, transform(BuildMbr(MbrMinX(mGeometry)-diff, MbrMinY(mGeometry)-diff,MbrMaxX(mGeometry)+ diff, MbrMaxY(mGeometry)+ diff, 3035),4326) buffered from (
select *,transform(Geometry,3035) mGeometry from
(select *,BuildMbr(x1, y1,x2, y2,4326) Geometry,BuildMbr(x1, y1,x2, y2,4326) Geometry from (
select  x1 , y1, x2 , y2, 50 diff, z,x,y from tiles_g
)))
) po 
where id in (
select pkid from idx_settlements_the_geom where pkid MATCH
RtreeIntersects(x1, y1,x2, y2))




union all
SELECT 2 _layer, ruid _id,AsBinary( case when Within(buffered, the_geom)=1 then buffered else (Intersection(buffered,the_geom)) end) geom,'highway='||hex('secondary')||';name='||hex(ifnull(rname,'')||ruid) _tags_name, z,x,y
FROM roads,(select x1, y1,x2, y2,z,x,y, transform(BuildMbr(MbrMinX(mGeometry)-diff, MbrMinY(mGeometry)-diff,MbrMaxX(mGeometry)+ diff, MbrMaxY(mGeometry)+ diff, 3035),4326) buffered from (
select *,transform(Geometry,3035) mGeometry from
(select *,BuildMbr(x1, y1,x2, y2,4326) Geometry,BuildMbr(x1, y1,x2, y2,4326) Geometry from (
select  x1 , y1, x2 , y2, 50 diff, z,x,y from tiles_g
where z>=15
)))
) po 
where ruid in (
select pkid from idx_roads_the_geom where pkid MATCH
RtreeIntersects(x1, y1,x2, y2))




union all

SELECT 3 _layer, buid _id,AsBinary( case when Within(buffered, the_geom)=1 then buffered else (Intersection(buffered,the_geom)) end) geom,case when has_customer=0 then 'emptybuilding' else 'nonemptybuilding' end|| '='||hex('bld') _tags_name, z,x,y
FROM buildings,(select x1, y1,x2, y2,z,x,y, transform(BuildMbr(MbrMinX(mGeometry)-diff, MbrMinY(mGeometry)-diff,MbrMaxX(mGeometry)+ diff, MbrMaxY(mGeometry)+ diff, 3035),4326) buffered from (
select *,transform(Geometry,3035) mGeometry from
(select *,BuildMbr(x1, y1,x2, y2,4326) Geometry,BuildMbr(x1, y1,x2, y2,4326) Geometry from (
select  x1 , y1, x2 , y2, 50 diff, z,x,y from tiles_g
where z>=15
)))
) po 
where buid in (
select pkid from idx_buildings_the_geom where pkid MATCH
RtreeIntersects(x1, y1,x2, y2))