alter table kiekot
  alter column hohto drop default,
  alter column hohto type bool using case when hohto=1 then true else false end;
alter table kiekot
  alter column spessu drop default,
  alter column spessu type bool using case when spessu=1 then true else false end;
alter table kiekot
  alter column dyed drop default,
  alter column dyed type bool using case when dyed=1 then true else false end;
alter table kiekot
  alter column swirly drop default,
  alter column swirly type bool using case when swirly=1 then true else false end;
alter table kiekot
  alter column myynnissa drop default,
  alter column myynnissa type bool using case when myynnissa=1 then true else false end;
alter table kiekot
  alter column loytokiekko drop default,
  alter column loytokiekko type bool using case when loytokiekko=1 then true else false end;
alter table kiekot
  alter column itb drop default,
  alter column itb type bool using case when itb=1 then true else false end;