--ON MAP SERVER
--create table maps.diff_customers as
select *,1 diftype from (select cusid,building_id from maps.v_billing_building_to_customers
EXCEPT 
select cusid,building_id from maps.building_to_customers) k
union all
select *,2 from (select cusid,building_id from maps.building_to_customers
EXCEPT 
select cusid,building_id from maps.v_billing_building_to_customers) k

select buid,has_customers from buildings b
where has_customers!=0 and not exists (select 1 from maps.building_to_customers c where b.buid=c.building_id)
union all
select buid,has_customers from buildings b
where has_customers=0 and exists (select 1 from maps.building_to_customers c where b.buid=c.building_id)


--insert into maps.building_to_customers (cusid,building_id)
--select cusid,building_id from maps.diff_customers


--ON BILLING SERVER
/*
 update customer set buid=null where buid  in (257193,
752636,
252364,
743100
);
update maps.building_to_customers c set cusid=cusid
where building_id
in (257193,
752636,
252364,
743100
);
 */
--create table maps.diff_customers as
select * from (

select *,1 diff from (select cusid,building_id from maps.building_to_customers c
EXCEPT
select cusid, buid from customer where buid is not null) k
union
select *,2 from (select cusid, buid from customer where buid is not null
EXCEPT
select cusid,building_id from maps.building_to_customers c) k) f





select distinct building_id,regionid,regid,subregionid, raiid  from maps.v_billing_customer_buildings
inner join buildings on building_id=buid
where has_customers!=0 and (regionid!=regid or subregionid !=raiid);

select buid,raiid,subregion_id from buildings b
inner join subregions s on  subregion_id is not null and intersects(s.the_geom,b.the_geom)
where (raiid !=subregion_id)
limit 5