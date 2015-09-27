<<<<<<< .mine
delete from maps.subregion_zoom_xy where subregion_id=&subregion_id;
=======
truncate table maps.subregionboundzx12;
insert into maps.subregionboundzx12
select subregion_id,zoom,x1,x2,y1,y2 from maps.v_subregion_minmax_zoom_xy
where subregion_id is not null;

delete from maps.subregion_zoom_xy where subregion_id=47;
>>>>>>> .r761
insert into maps.subregion_zoom_xy(subregion_id,zoom,x,y)
select subregion_id,(f).zoom,(f).x,(f).y from (
select subregion_id,maps.createGeometryzxy(the_geom,8,18,false) f from subregions 
where subregion_id=&subregion_id )k;

delete  from maps.zoom_xy zxy
where exists (select 1 from  maps.subregion_zoom_xy sr where sr.subregion_id=&subregion_id and (sr.zoom,sr.x,sr.y)=(zxy.zoom,zxy.x,zxy.y));

insert into maps.zoom_xy(zoom,x,y)
select zoom,x,y from maps.subregion_zoom_xy sr where sr.subregion_id=&subregion_id;
select maps.creatercnnew(3000) from generate_series(1, 200);


/*
create table maps.subregion_zoom_xy as 
select subregion_id,zoom,generate_series(x1, x2) x, y from 
(select subregion_id,zoom::integer zoom,x1,x2,generate_series(y1, y2) y from maps.v_subregion_minmax_zoom_xy) k ;
ALTER TABLE maps.subregion_zoom_xy ADD COLUMN id serial;
ALTER TABLE maps.subregion_zoom_xy ADD PRIMARY KEY (id);
ALTER TABLE maps.subregion_zoom_xy ADD COLUMN bbox_values double precision[];

update maps.subregion_zoom_xy set bbox_values=maps.get_tile_bbox(x,y,zoom::integer);
SELECT AddGeometryColumn ('maps','subregion_zoom_xy','the_geom',32638,'MULTIPOLYGON',2);
update maps.subregion_zoom_xy set the_geom=
 ST_Multi(ST_TRANSFORM(ST_GeomFromText(format('POLYGON((%s %s, %s %s, %s %s, %s %s, %s %s))',
bbox_values[1], bbox_values[2], bbox_values[1], 
bbox_values[4], bbox_values[3], bbox_values[4], 
bbox_values[3], bbox_values[2], bbox_values[1], 
bbox_values[2]),900913),32638)) ;

CREATE INDEX subregion_zoom_xy_the_geom_idx
  ON maps.subregion_zoom_xy
  USING gist
  (the_geom );
  
  
update subregions k set (minx,miny,maxx,maxy)=(xmin(k.the_geom::box3d), ymin(k.the_geom::box3d)  , xmax(k.the_geom::box3d) , ymax(k.the_geom::box3d));

  
create table maps.subregion_zoom_xy_compare as
select id,k.subregion_id,minx,miny,maxx,maxy, xmin(k.the_geom::box3d) xmink, ymin(k.the_geom::box3d) ymink , xmax(k.the_geom::box3d) xmaxk, ymax(k.the_geom::box3d) ymaxk from maps.subregion_zoom_xy k
inner join subregions sr on sr.subregion_id=k.subregion_id;

alter table maps.subregion_zoom_xy_compare add column intersects boolean;

update maps.subregion_zoom_xy_compare set intersects=maps.get_coords_intersects(minx ,
  miny ,
  maxx ,
  maxy ,
  xmink ,
  ymaxk ,
  xmaxk ,
  ymink );
  
delete from maps.subregion_zoom_xy k
where id in (select s.id from maps.subregion_zoom_xy_compare s where not s.intersects );
drop table maps.subregion_zoom_xy_compare;


CREATE TABLE maps.zoom_xy
(
  id serial NOT NULL,
  zoom double precision,
  x integer,
  y integer,
  bbox_values double precision[],
  proceeded boolean DEFAULT false,
  img_data bytea,
  rcn_id integer,
  CONSTRAINT zoom_xy_pkey PRIMARY KEY (id )
);

insert into maps.zoom_xy (
zoom ,
  x ,
  y ,
  bbox_values ,
  the_geom )
select zoom ,
  x ,
  y ,
  min(bbox_values) ,
  min(the_geom )
  from maps.subregion_zoom_xy
group by zoom,x,y;*/