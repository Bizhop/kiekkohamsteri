COPY dd_arvot (id, valikko, nimi, arvo) FROM stdin;
16	kunto	Käyttämätön (10)	10
18	kunto	Kokeiltu (9)	9
19	tussit	Ei ole	1
20	tussit	Rimmissä	2
21	tussit	Pohjassa	3
22	tussit	Rimmi + pohja	4
23	tussit	Kannessa	5
24	tussit	Kaikkialla	6
25	kunto	Käytetty (8)	8
26	kunto	Kulunut (7)	7
27	kunto	Reilusti kulunut (6)	6
28	kunto	Rolleri (5)	5
29	kunto	Jätettä (4)	4
\.

SELECT pg_catalog.setval('dd_arvot_id_seq', 30, false);

COPY members (id, username, email, level, etunimi, sukunimi, pdga_num) FROM stdin;
2	pekkanyk	qru1982@gmail.com	2	Pekka	Nykänen	90313
28	kkiki	kiti.eerola@gmail.com	1	Kiti	Eerola	79819
16	Hormoni	hormoni@gmail.com	2	Tuomas	Sievers	70697
27	kurre	juha.orava@gmail.com	1	Juha	Orava	79889
1	Bisse	ville.piispa@gmail.com	2	Ville	Piispa	90315
\.

SELECT pg_catalog.setval('members_id_seq', 28, true);

COPY r_valm (id, valmistaja) FROM stdin;
1	ABC Discs
2	Aerobie
3	ANY
4	Axiom Discs
5	DGA
6	Discmania
7	Discraft
8	Dynamic Discs
9	Gateway
10	Hyzer Bomb
11	Innova
12	Kastaplast
13	Las Aves
14	Latitude 64
15	Legacy
16	Millennium
17	MVP
18	Prodigy
19	Prodiscus
20	Salient Discs
21	Vibram
22	Viking Discs
23	Westside Discs
24	Yikun Sports
\.

SELECT pg_catalog.setval('r_valm_id_seq', 25, false);

COPY r_mold (id, valmistaja_id, kiekko, nopeus, liito, vakaus, feidi) FROM stdin;
568	11	Tern (star)	12.0	6.0	-3.0	2.0
569	6	FD	7.0	6.0	-1.0	1.0
570	11	Destroyer	12.0	5.0	-1.0	3.0
571	7	Buzzz	5.0	4.0	-1.0	1.0
572	7	Buzzz OS	5.0	4.0	0.0	3.0
573	18	Pa3	2.0	2.0	-1.0	1.0
574	18	Pa1	2.0	2.0	0.0	2.0
575	18	Pa2	2.0	2.0	0.0	1.0
576	11	Beast-X	10.0	5.0	-2.0	2.0
577	11	Firebird	9.0	3.0	0.0	4.0
578	19	LASERi	9.0	4.0	-1.0	2.0
579	24	Jun	11.0	5.0	-1.0	2.0
580	24	Hu	9.0	5.0	-2.0	2.0
581	24	Gou	8.0	5.0	-4.0	1.0
582	24	Da E	13.0	5.0	0.0	3.0
583	12	Grym	13.0	5.0	-2.0	2.0
584	19	JOKERi	4.0	3.0	0.0	2.0
585	11	Colossus	14.0	5.0	-2.0	3.0
586	11	Vulcan	13.0	5.0	-4.0	2.0
587	11	Shryke	13.0	6.0	-2.0	2.0
588	11	Katana	13.0	5.0	-3.0	3.0
589	11	Groove	13.0	6.0	-2.0	2.0
590	11	Dominator	13.0	5.0	-1.0	2.0
591	11	Daedalus	13.0	6.0	-3.0	2.0
592	11	Boss	13.0	5.0	-1.0	3.0
593	11	Ape	13.0	5.0	0.0	4.0
594	11	XCaliber	12.0	5.0	0.0	4.0
595	11	Wahoo	12.0	6.0	-2.0	2.0
596	11	Tern	12.0	6.0	-3.0	2.0
597	11	Teedevil	12.0	5.0	-1.0	2.0
598	11	Wraith	11.0	5.0	-1.0	3.0
599	11	Teerex	11.0	4.0	0.0	4.0
600	11	Max	11.0	3.0	0.0	5.0
601	11	Mamba	11.0	6.0	-5.0	1.0
602	11	Krait	11.0	5.0	-1.0	2.0
603	11	Archon	11.0	5.0	-2.0	2.0
604	11	Starfire	10.0	4.0	0.0	3.0
605	11	Orc	10.0	4.0	-1.0	3.0
606	11	Monster	10.0	3.0	0.0	5.0
607	11	Monarch	10.0	5.0	-4.0	1.0
608	11	Beast	10.0	5.0	-2.0	2.0
609	11	Vikings	9.0	4.0	-1.0	2.0
610	11	Valkyrie	9.0	4.0	-2.0	2.0
611	11	Thunderbird	9.0	5.0	0.0	2.0
612	11	Sidewinder	9.0	5.0	-3.0	1.0
613	11	Roadrunner	9.0	5.0	-4.0	1.0
614	11	FL	9.0	3.0	0.0	2.0
615	11	TL3	8.0	4.0	0.0	2.0
616	11	Teebird3	8.0	4.0	0.0	2.0
617	11	Dragon	8.0	5.0	-2.0	2.0
618	11	Archangel	8.0	6.0	-4.0	1.0
619	11	TL	7.0	5.0	0.0	1.0
620	11	Teebird	7.0	5.0	0.0	2.0
621	11	Leopard3	7.0	5.0	-2.0	1.0
622	11	Eagle	7.0	4.0	-1.0	3.0
623	11	Banshee	7.0	3.0	0.0	3.0
624	11	Whippet	6.0	3.0	1.0	5.0
625	11	Viper	6.0	4.0	1.0	5.0
626	11	Leopard	6.0	5.0	-2.0	1.0
627	11	Gazelle	6.0	4.0	0.0	2.0
628	11	Cheetah	6.0	4.0	-2.0	2.0
629	11	Wombat	5.0	6.0	-1.0	0.0
630	11	VCobra	5.0	5.0	-1.0	2.0
631	11	Spider	5.0	3.0	0.0	1.0
632	11	Skeeter	5.0	5.0	-1.0	1.0
633	11	Shark3	5.0	4.0	0.0	2.0
634	11	Roc3	5.0	4.0	0.0	3.0
635	11	Panther	5.0	4.0	-2.0	1.0
636	11	Manta	5.0	5.0	-2.0	2.0
637	11	Mako3	5.0	5.0	0.0	0.0
638	11	Kite	5.0	6.0	-3.0	1.0
639	11	Gator	5.0	2.0	0.0	3.0
640	11	Foxbat	5.0	6.0	-1.0	0.0
641	11	Cro	5.0	3.0	0.0	2.0
642	11	Atlas	5.0	4.0	0.0	1.0
643	11	Wolf	4.0	3.0	-4.0	1.0
644	11	VRoc	4.0	4.0	0.0	1.0
645	11	Stingray	4.0	5.0	-3.0	1.0
646	11	Shark	4.0	4.0	0.0	2.0
647	11	Roc+	4.0	5.0	0.0	2.0
648	11	Roc	4.0	4.0	0.0	3.0
649	11	Mako	4.0	5.0	0.0	0.0
650	11	King Cobra	4.0	5.0	0.0	2.0
651	11	Coyote	4.0	5.0	0.0	1.0
652	11	Cobra	4.0	5.0	-2.0	2.0
653	11	XD	3.0	4.0	-1.0	1.0
654	11	Stud	3.0	3.0	0.0	2.0
655	11	Pig	3.0	1.0	0.0	3.0
656	11	Mirage	3.0	4.0	-3.0	0.0
657	11	Hydra	3.0	3.0	0.0	2.0
658	11	Dart	3.0	4.0	0.0	0.0
659	11	Colt	3.0	4.0	-1.0	1.0
660	11	Aero	3.0	6.0	0.0	0.0
661	11	Yeti Aviar	2.0	3.0	0.0	1.0
662	11	Whale	2.0	3.0	0.0	1.0
663	11	Rhyno	2.0	1.0	0.0	3.0
664	11	Nova	2.0	3.0	0.0	0.0
665	11	KC Aviar	2.0	3.0	0.0	2.0
666	11	JK Aviar	2.0	3.0	0.0	2.0
667	11	Classic Aviar	2.0	3.0	0.0	0.0
668	11	Aviar Driver	2.0	3.0	0.0	2.0
669	11	Aviar	2.0	3.0	0.0	1.0
670	11	Sonic	1.0	2.0	-4.0	0.0
671	11	Polecat	1.0	3.0	0.0	0.0
672	11	Birdie	1.0	2.0	0.0	0.0
673	6	P1	2.0	3.0	0.0	0.0
674	6	Px1	2.0	3.0	0.0	0.0
675	6	P2	2.0	3.0	0.0	1.0
676	6	P3	3.0	2.0	0.0	3.0
677	6	MD	4.0	5.0	0.0	0.0
678	6	MD1	4.0	4.0	0.0	2.0
679	6	MD2	4.0	5.0	0.0	2.0
680	6	MD3	5.0	5.0	0.0	3.0
681	6	GM	5.0	4.0	0.0	2.0
682	6	FD2	7.0	4.0	0.0	2.0
683	6	FD3	9.0	4.0	0.0	3.0
684	6	CD2	9.0	5.0	-1.0	2.0
685	6	CD	10.0	5.0	-1.0	2.0
686	6	TD	10.0	5.0	-2.0	1.0
687	6	TD2	10.0	5.0	-4.0	1.0
688	6	PD	10.0	4.0	0.0	3.0
689	6	DD	11.0	5.0	0.0	2.0
690	6	PD2	12.0	4.0	0.0	4.0
691	6	DDx	12.0	6.0	-1.0	2.0
692	6	DD2	13.0	5.0	-2.0	2.0
693	23	Swan 1 Reborn	3.0	3.0	-1.0	0.0
694	23	Sling	5.0	5.0	0.0	1.0
695	23	Longbowman	9.0	4.0	0.0	3.0
696	23	Sampo	10.0	5.0	0.0	2.0
697	23	Queen	14.0	5.0	-2.0	2.0
698	23	Catapult	14.0	5.0	0.0	3.0
699	23	Destiny	14.0	6.0	-2.0	3.0
700	23	Underworld	7.0	6.0	-3.0	1.0
701	23	Hatchet	9.0	6.0	-2.0	2.0
702	23	Stag	8.0	6.0	0.0	3.0
703	23	Northman	10.0	5.0	-1.0	2.0
704	23	Boatman	11.0	5.0	0.0	2.0
705	11	Vedge	4.0	3.0	-3.0	1.0
706	23	Sword	12.0	5.0	0.0	2.0
707	23	Giant	13.0	5.0	1.0	4.0
708	23	Sorcerer	13.0	6.0	0.0	2.0
709	23	Swan 2	3.0	3.0	-1.0	0.0
710	23	Shield	3.0	3.0	0.0	1.0
711	23	Warship	6.0	5.0	0.0	1.0
712	23	Bard	5.0	4.0	0.0	3.0
713	23	World	14.0	4.0	1.0	4.0
714	23	King	14.0	5.0	-1.0	4.0
715	23	Harp	4.0	3.0	0.0	3.0
716	23	Tursas	5.0	5.0	-3.0	1.0
717	23	Seer	7.0	5.0	-2.0	1.0
718	14	Ballista	14.0	4.0	-1.0	4.0
719	14	Bolt	13.0	6.0	-2.0	3.0
720	14	Cutlass	13.0	5.0	0.0	3.0
721	14	Flow	11.0	6.0	-1.0	2.0
722	14	Gladiator	13.0	5.0	0.0	3.0
723	14	Halo	13.0	5.0	-1.0	3.0
724	14	Havoc	13.0	5.0	-1.0	3.0
725	14	Knight	14.0	4.0	-1.0	4.0
726	14	Missilen	15.0	3.0	1.0	5.0
727	14	Raketen	15.0	3.0	-2.0	4.0
728	14	Scythe	12.0	3.0	0.0	4.0
729	14	Stiletto	13.0	2.0	1.0	6.0
730	14	Blitz	11.0	3.0	0.0	4.0
731	14	Villain	12.0	4.0	0.0	4.0
732	14	Riot	11.0	4.0	-1.0	2.0
733	14	Culverin	9.0	5.0	0.0	3.0
734	14	Falchion	9.0	6.0	-1.0	2.0
735	14	Fury	9.0	6.0	-2.0	2.0
736	14	Maul	7.0	7.0	-2.0	1.0
737	14	River	7.0	7.0	-1.0	1.0
738	14	Saint	9.0	7.0	-1.0	2.0
739	14	Saint Pro	8.0	6.0	0.0	3.0
740	14	Spark	7.0	4.0	0.0	3.0
741	14	Striker	9.0	5.0	0.0	2.0
742	14	XXX	7.0	2.0	0.0	5.0
743	14	Trident	5.0	2.0	0.0	5.0
744	14	Vision	8.0	6.0	-1.0	2.0
745	14	Anchor	5.0	4.0	0.0	3.0
746	14	Claymore	5.0	5.0	-1.0	1.0
747	14	Compass	5.0	5.0	0.0	1.0
748	14	Core	5.0	5.0	-1.0	2.0
749	14	Fuse	5.0	6.0	-2.0	1.0
750	14	Mace	5.0	5.0	0.0	2.0
751	14	Pain	4.0	4.0	0.0	3.0
752	14	Caltrop	2.0	2.0	0.0	2.0
753	14	Dagger	2.0	4.0	0.0	2.0
754	14	Gauntlet	2.0	5.0	0.0	1.0
755	14	Macana	2.0	5.0	0.0	1.0
756	14	Mercy	2.0	5.0	0.0	1.0
757	14	Pure	3.0	3.0	0.0	1.0
758	14	Sinus	2.0	1.0	0.0	3.0
759	14	Spike	4.0	2.0	-1.0	2.0
760	14	Jade	9.0	6.0	-2.0	1.0
761	14	Diamond	8.0	6.0	-3.0	1.0
762	14	Pearl	4.0	6.0	-4.0	1.0
763	14	Ruby	3.0	5.0	-3.0	1.0
764	8	Freedom	14.0	5.0	-2.0	3.0
765	8	Defender	13.0	5.0	0.0	3.0
766	8	Sheriff	13.0	5.0	-1.0	2.0
767	8	Enforcer	12.0	4.0	1.0	4.0
768	8	Trespass	12.0	5.0	-1.0	3.0
769	8	Renegade	11.0	5.0	-2.0	3.0
770	8	Felon	9.0	3.0	1.0	4.0
771	8	Convict	9.0	4.0	-1.0	3.0
772	8	Escape	9.0	5.0	-1.0	2.0
773	8	Thief	8.0	5.0	0.0	2.0
774	8	Witness	8.0	6.0	-3.0	1.0
775	8	Justice	5.0	1.0	1.0	4.0
776	8	Verdict	5.0	4.0	0.0	4.0
777	8	EMAC Truth	5.0	5.0	0.0	2.0
778	8	Truth	5.0	5.0	-1.0	1.0
779	8	Fugitive	5.0	5.0	-1.0	2.0
780	8	Evidence	5.0	5.0	-1.0	0.0
781	8	Warrant	5.0	5.0	-2.0	0.0
782	8	Suspect	4.0	3.0	0.0	3.0
783	8	Marshal	3.0	4.0	0.0	1.0
784	8	Judge	2.0	4.0	0.0	1.0
785	8	Warden	2.0	4.0	0.0	1.0
786	8	Breakout	8.0	5.0	-1.0	2.0
787	8	Proof	5.0	6.0	-3.0	1.0
788	8	Gavel	3.0	5.0	-2.0	1.0
789	17	Anode	3.0	4.0	0.0	0.0
790	17	Amp	8.0	5.0	-2.0	1.0
791	17	Atom	3.0	4.0	-1.0	0.0
792	17	Axis	5.0	5.0	-1.0	1.0
793	17	Impulse	11.0	5.0	-3.0	1.0
794	17	Inertia	11.0	5.0	-2.0	2.0
795	17	Ion	3.0	4.0	0.0	1.0
796	17	Motion	11.0	4.0	0.0	4.0
797	17	Phase	12.0	3.0	0.0	5.0
798	17	Photon	12.0	5.0	-1.0	3.0
800	17	Resistor	7.0	4.0	0.0	4.0
801	17	Servo	6.0	5.0	-1.0	2.0
802	17	Shock	8.0	5.0	0.0	3.0
803	17	Switch	6.0	4.0	-2.0	1.0
804	17	Tangent	5.0	5.0	-2.0	1.0
805	17	Tensor	5.0	4.0	0.0	3.0
806	17	Tesla	11.0	5.0	-1.0	2.0
807	17	Vector	5.0	4.0	0.0	3.0
808	17	Volt	8.0	5.0	-1.0	2.0
809	17	Wave	12.0	5.0	-2.0	1.0
810	4	Alias	5.0	5.0	0.0	1.0
811	4	Clash	6.0	4.0	-1.0	3.0
812	4	Crave	6.0	5.0	-1.0	2.0
813	4	Envy	2.0	3.0	0.0	2.0
814	4	Fireball	10.0	3.0	0.0	4.0
815	4	Insanity	10.0	5.0	-3.0	1.0
816	4	Inspire	7.0	5.0	-3.0	1.0
817	4	Proxy	3.0	4.0	-1.0	0.0
818	4	Theory	5.0	5.0	-3.0	1.0
819	4	Virus	10.0	5.0	-4.0	1.0
820	4	Wrath	10.0	5.0	-1.0	2.0
821	7	APX	2.0	2.0	-1.0	1.0
822	7	Avenger	10.0	5.0	0.0	3.0
823	7	Avenger SS	10.0	5.0	-3.0	1.0
824	7	Banger GT	2.0	3.0	0.0	1.0
825	7	Breeze	4.0	4.0	-2.0	1.0
826	7	Buzzz SS	5.0	4.0	-2.0	1.0
827	7	Challenger	2.0	3.0	0.0	2.0
828	7	Comet	4.0	4.0	-2.0	1.0
829	7	Crank	13.0	5.0	-1.0	3.0
830	7	Crush	11.0	5.0	0.0	4.0
831	7	Cyclone	7.0	4.0	-1.0	3.0
832	7	Drone	5.0	3.0	0.0	4.0
833	7	Eclipse	7.0	4.0	-2.0	2.0
834	7	Flash	10.0	5.0	-2.0	3.0
835	7	Flick	9.0	4.0	1.0	4.0
836	7	Focus	2.0	2.0	-1.0	2.0
837	7	Force	12.0	5.0	0.0	4.0
838	7	Glide	7.0	5.0	-3.0	1.0
839	7	Hawk	4.0	3.0	-2.0	2.0
840	7	Heat	9.0	5.0	-4.0	1.0
841	7	Hornet	5.0	5.0	0.0	4.0
842	7	Impact	6.0	5.0	-2.0	2.0
843	7	Magnet	2.0	3.0	-1.0	2.0
844	6	CD3	11.0	5.0	-1.0	2.0
845	12	Berg	1.0	3.0	0.0	2.0
846	6	PDx	11.0	4.0	0.0	3.0
847	7	Tracker	8.0	5.0	-1.0	3.0
848	19	Sparta	4.0	4.0	-1.0	1.0
849	16	Orion LF	9.0	5.0	-1.0	2.0
850	24	Wings	3.0	3.0	0.0	2.0
851	9	Wizard	2.0	2.0	0.0	2.0
853	14	Beetle	1.0	7.0	-1.0	0.0
854	7	Zone	4.0	3.0	0.0	3.0
855	7	Surge	10.0	5.0	-1.0	3.0
856	19	LEGENDa	13.0	5.0	-1.0	3.0
857	19	MIDARi	5.0	4.0	-1.0	2.0
858	19	RESPECTi	8.0	4.0	0.0	2.0
859	19	SLAIDi	12.0	4.0	0.0	3.0
860	19	ROCKET	9.0	4.0	0.0	2.0
861	19	TITAN	10.0	4.0	0.0	2.0
862	7	Zombee	7.0	6.0	-1.0	2.0
863	7	Undertaker	9.0	5.0	-1.0	2.0
864	7	Predator	9.0	4.0	0.0	4.0
865	20	Flatline	6.0	5.0	-1.0	1.0
866	7	Stalker	8.0	5.0	-1.0	1.0
867	7	Mantis	8.0	4.0	-1.0	2.0
868	7	Nuke	13.0	4.0	-1.0	3.0
869	7	Meteor	4.0	5.0	-3.0	1.0
870	12	Reko	3.0	3.0	0.0	1.0
871	12	Kaxe Z	6.0	5.0	0.0	2.0
872	7	Buzzz GT	5.0	4.0	-1.0	1.0
873	16	Scorpius	12.0	5.0	-1.0	3.0
874	6	P1x	2.0	3.0	0.0	0.0
875	14	Medius	6.0	6.0	0.0	2.0
876	7	Reaper	8.0	4.0	0.0	4.0
877	7	Storm	8.0	4.0	0.0	3.0
878	7	XL	7.0	4.0	-1.0	3.0
879	7	Xpress	8.0	5.0	-3.0	1.0
880	7	XS	7.0	4.0	-2.0	3.0
881	7	Puttr	2.0	2.0	-1.0	1.0
882	7	Rattler	2.0	2.0	0.0	1.0
883	7	Ringer	4.0	4.0	0.0	2.0
884	7	Nebula	5.0	5.0	-1.0	3.0
885	7	Stratus	5.0	4.0	-3.0	1.0
886	7	Wasp	5.0	4.0	0.0	3.0
887	7	Nuke OS	13.0	4.0	0.0	4.0
888	7	Nuke SS	13.0	4.0	-3.0	3.0
889	7	Pulse	11.0	5.0	0.0	3.0
890	7	Spectra	10.0	5.0	-2.0	3.0
891	7	Surge SS	10.0	5.0	-1.0	2.0
892	7	Wildcat	10.0	4.0	-2.0	3.0
893	3	ANY	0.0	0.0	0.0	0.0
894	9	Apache	9.0	6.0	-1.0	2.0
895	9	Assassin	9.0	6.0	-2.0	1.0
896	9	Blaze	7.0	5.0	-1.0	3.0
897	9	Blurr	10.0	5.0	-1.0	3.0
898	9	Chief	3.0	2.0	0.0	1.0
899	9	Chief OS	4.0	2.0	0.0	2.0
900	9	Demon	4.0	4.0	0.0	3.0
901	9	Devil Hawk	3.0	2.0	0.0	1.0
902	9	Diablo DT	11.0	6.0	-1.0	4.0
903	9	Element	5.0	5.0	-2.0	1.0
904	9	Element-X	5.0	4.0	-2.0	3.0
905	9	Hybrid	8.0	5.0	-1.0	3.0
906	9	Illusion	10.0	5.0	-1.0	3.0
907	9	Karma	6.0	5.0	-1.0	2.0
908	9	Magic	2.0	3.0	-1.0	0.0
909	9	Mystic	5.0	5.0	-2.0	2.0
910	9	Rage	10.0	6.0	-1.0	3.0
911	9	Sabre	7.0	5.0	-2.0	2.0
912	9	Samurai	13.0	6.0	-2.0	2.0
913	9	Savage	10.0	5.0	0.0	3.0
914	9	Scout	8.0	6.0	0.0	1.0
915	9	Shaman	3.0	3.0	-2.0	0.0
916	9	Slayer	13.0	5.0	-2.0	2.0
917	9	Speed Demon	9.0	4.0	0.0	4.0
918	9	Spirit	10.0	6.0	1.0	4.0
919	9	Voodoo	3.0	3.0	0.0	1.0
920	9	War Spear	3.0	3.0	-2.0	0.0
921	9	Warlock	2.0	3.0	0.0	1.0
922	9	Warrior	4.0	4.0	-1.0	3.0
923	10	MOAB	6.0	4.0	1.0	5.0
924	10	Mortar	5.0	2.0	0.0	3.0
925	10	Panzer Tank	2.0	1.0	0.0	3.0
926	10	Tank	2.0	1.0	0.0	2.0
927	10	Veteran	7.0	4.0	0.0	1.0
928	12	Kaxe	6.0	5.0	0.0	3.0
929	12	Rask	13.0	4.0	0.0	5.0
930	15	Bandit	9.0	5.0	-2.0	1.0
931	15	Cannon	14.0	5.0	-3.0	3.0
932	15	Clozer	2.0	3.0	0.0	2.0
933	15	Clutch	2.0	3.0	0.0	1.0
934	15	Gauge	5.0	5.0	0.0	1.0
935	15	Ghost	4.0	5.0	0.0	3.0
936	15	Hunter	2.0	4.0	0.0	0.0
937	15	Mongoose	9.0	5.0	-3.0	1.0
938	15	Nemesis	10.0	6.0	-4.0	2.0
939	15	Outlaw	12.0	5.0	-1.0	3.0
940	15	Patriot	7.0	5.0	-2.0	1.0
941	15	Prowler	2.0	3.0	0.0	2.0
942	15	Rampage	14.0	5.0	-1.0	4.0
943	16	Aries	13.0	6.0	-3.0	1.0
944	16	Astra	11.0	5.0	-2.0	2.0
945	16	Astra X	11.0	4.0	-3.0	1.0
946	16	Aurora MF	4.0	5.0	0.0	2.0
947	16	Aurora MS	4.0	5.0	0.0	0.0
948	16	EXP 1	7.0	3.0	1.0	4.0
949	16	Omega	2.0	3.0	0.0	0.0
950	16	Omega Big Bead	2.0	3.0	0.0	2.0
951	16	Orion LS	9.0	4.0	-1.0	2.0
952	16	Polaris LS	8.0	4.0	-1.0	1.0
953	16	Quasar	13.0	5.0	-1.0	4.0
954	16	Sentinel MF	5.0	4.0	0.0	4.0
955	19	RAZERi	12.0	5.0	0.0	3.0
956	19	STARi	5.0	4.0	-2.0	0.0
957	18	D1	13.0	6.0	-1.0	3.0
958	18	D2	13.0	6.0	-1.0	3.0
959	18	D3	13.0	5.0	-2.0	2.0
960	18	D4	13.0	6.0	-3.0	2.0
961	18	D5	13.0	5.0	-4.0	2.0
962	18	F1	7.0	4.0	0.0	4.0
963	18	F2	7.0	4.0	-1.0	3.0
964	18	F3	7.0	5.0	-2.0	2.0
965	18	F5	8.0	5.0	-1.0	2.0
966	18	F7	7.0	5.0	-3.0	1.0
967	18	H1	10.0	4.0	1.0	1.0
968	18	H2	10.0	5.0	0.0	3.0
969	18	H3	10.0	5.0	-1.0	3.0
970	18	H4	10.0	6.0	-1.0	3.0
971	18	M1	5.0	4.0	0.0	4.0
972	18	M2	5.0	4.0	0.0	2.0
973	18	M3	5.0	4.0	-1.0	2.0
974	18	M4	5.0	5.0	-1.0	1.0
975	18	M5	5.0	5.0	-3.0	1.0
976	18	Pa4	2.0	3.0	-1.0	1.0
977	18	X1	13.0	3.0	0.0	5.0
978	20	Antidote	5.0	6.0	-2.0	1.0
979	20	Backdraft	12.0	5.0	-2.0	2.0
980	20	Cam Todd Touch	2.0	3.0	0.0	1.0
981	20	Cell	5.0	5.0	0.0	2.0
982	20	Fracture	10.0	5.0	-3.0	1.0
983	20	Lock Jaw	1.0	4.0	0.0	0.0
984	20	Napalm	11.0	5.0	-2.0	2.0
985	20	Prometheus	13.0	5.0	-1.0	1.0
986	20	Reign	13.0	5.0	0.0	2.0
987	20	Suture	9.0	5.0	-2.0	0.0
988	20	Vaccine	3.0	3.0	0.0	2.0
989	20	Vein	8.0	4.0	0.0	2.0
990	24	Claws	1.0	3.0	0.0	1.0
991	24	Gui	2.0	3.0	0.0	3.0
992	24	View	7.0	6.0	-1.0	1.0
993	24	Yao	4.0	4.0	0.0	2.0
994	1	Bee Line	13.0	6.0	-2.0	2.0
995	1	Flying Squirrel	4.0	5.0	-4.0	1.0
996	1	Gamma Ray	9.0	4.0	-2.0	2.0
997	1	Mission	4.0	4.0	-1.0	2.0
998	1	Money	2.0	3.0	0.0	2.0
999	1	Secret Weapon	9.0	4.0	-3.0	2.0
1000	2	Arrow	3.0	2.0	0.0	2.0
1001	2	Epic	10.0	3.0	-2.0	4.0
1002	2	Sharpshooter #1	7.0	3.0	0.0	3.0
1003	2	Sharpshooter #2	4.0	3.0	0.0	3.0
1004	2	Sharpshooter #3	2.0	3.0	0.0	3.0
1005	5	Aftershock	4.0	3.0	-1.0	1.0
1006	5	BlowFly	2.0	2.0	-1.0	2.0
1007	5	BlowFly II	2.0	2.0	-2.0	1.0
1008	5	Breaker	3.0	3.0	0.0	3.0
1009	5	Flathead Cyclone	7.0	4.0	-1.0	3.0
1010	5	Hellfire	10.0	1.0	0.0	5.0
1011	5	Hurricane	12.0	5.0	-1.0	2.0
1012	5	Reef	2.0	2.0	-1.0	1.0
1013	5	Riptide	9.0	4.0	0.0	3.0
1014	5	Rogue	10.0	5.0	-1.0	3.0
1015	5	Shockwave	4.0	2.0	0.0	3.0
1016	5	Squall	4.0	4.0	-1.0	2.0
1017	5	Steady	2.0	3.0	0.0	5.0
1018	5	Titanic	2.0	3.0	-1.0	3.0
1019	5	Torrent	14.0	5.0	-2.0	3.0
1020	5	Tsunami	10.0	4.0	0.0	4.0
1021	5	Undertow	9.0	5.0	-2.0	2.0
1022	13	Ka-Kaa	12.0	5.0	-2.0	2.0
1023	13	Wo-hoo	6.0	5.0	0.0	2.0
1024	21	Ascent	8.0	5.0	0.0	3.0
1025	21	Four20	13.0	4.0	0.0	4.0
1026	21	Ibex	5.0	4.0	-2.0	2.0
1027	21	Lace	13.0	6.0	-1.0	3.0
1028	21	Obex	5.0	5.0	0.0	3.0
1029	21	O-Lace	14.0	5.0	0.0	4.0
1030	21	Ridge	2.0	4.0	0.0	1.0
1031	21	Sole	2.0	3.0	0.0	2.0
1032	21	Summit	3.0	3.0	-1.0	0.0
1033	21	Trak	7.0	5.0	-1.0	1.0
1034	21	unLace	13.0	6.0	-3.0	1.0
1035	21	Valley	7.0	5.0	-1.0	1.0
1036	21	VP	2.0	3.0	0.0	3.0
1037	22	Knive	2.0	3.0	0.0	2.0
1038	22	Cosmos	7.0	5.0	0.0	1.0
1039	22	Axe	4.0	3.0	0.0	1.0
1040	22	Berserger	10.0	5.0	-3.0	2.0
1041	22	Nordic Warrior	4.0	4.0	0.0	2.0
1042	22	Ragnarok	11.0	5.0	-1.0	2.0
1043	22	Rune	2.0	4.0	0.0	0.0
1044	22	Thunder God Thor	14.0	5.0	0.0	2.0
1045	23	Crown	3.0	4.0	0.0	1.0
1046	8	Slammer	3.0	2.0	0.0	3.0
1047	6	MD4	5.0	4.0	0.0	3.0
1048	6	TDx	9.0	5.0	-3.0	1.0
1049	6	P3x	3.0	2.0	0.0	3.0
1050	11	Aviar3	3.0	2.0	0.0	2.0
1051	11	AviarX3	3.0	2.0	0.0	3.0
\.

SELECT pg_catalog.setval('r_mold_id_seq', 1051, true);

COPY r_muovi (id, muovi, valmistaja_id) FROM stdin;
1	 Eclipse Proton	17
2	$$$	9
3	200	18
4	300	18
5	350 Light	18
6	350G	18
7	400	18
8	400G	18
9	400G Light	18
10	450	18
11	750	18
12	Air	18
13	ANY	3
14	Base	10
15	Base Soft	10
16	Basic	19
17	BioFuzion	8
18	Blizzard Champion	11
19	Bronze	1
20	BT Hard	23
21	BT Medium	23
22	BT Megasoft	23
23	BT Soft	23
24	C-Line	6
25	Champion	11
26	Classic	8
27	Classic Blend	8
28	Classic Soft	8
29	Classic Super Soft	8
30	Crazy Tuff	7
31	Cryztal FLX	7
32	D-Line	6
33	Dragon	24
34	DT	16
35	DX	11
36	Echo Star	11
37	ED	9
38	Elasto	23
39	Electron	17
40	Elite X	7
41	Elite Z	7
42	Elite Z Lite	7
43	EP	9
44	ER	9
45	ESP	7
46	ET	16
47	EVO	9
48	Excel	15
49	Fission	17
50	Fluid	8
51	Frontline	10
52	Frontline-X	10
53	Frost	14
54	Fuzion	8
55	G-Line	6
56	Glo (Z)	7
57	GLOW	9
58	Gold	14
59	Gold	1
60	Gravity	15
61	Ground	22
62	GStar	11
63	HD	9
64	Icon	15
65	Jawbreaker	7
66	JK Pro	11
67	K1	12
68	K2	12
69	K3	12
70	KC Pro	11
71	Liquid	20
72	Liquid Metal	20
73	Lucid	8
74	Lucid Air	8
75	Lunar	16
76	McPro	11
77	Metal Flake	11
78	Moonshine	14
79	Moonshine (Frost)	14
80	Moonshine (Opto)	14
81	Neutron	17
82	Opto	14
83	Opto Air	14
84	ORG	9
85	P-Line	6
86	Phoenix	24
87	Pinnacle	15
88	Plasma	17
89	Platinum	1
90	Premium	19
91	Prime	8
92	Pro	11
93	Pro D	7
94	Protege	15
95	Proton	17
96	Quantum	16
97	Quantum Zero-G	16
98	R-Pro	11
99	Recon	10
100	Reprocessed	14
101	Retro	14
102	S	9
103	S-Line	6
104	SGS	9
105	Sirius	16
106	Snow	14
107	Spill	14
108	Standard	16
109	Star	11
110	Starlite	11
111	Storm	22
112	Supersoft	16
113	Ti	7
114	Tiger	24
115	Tournament	23
116	Ultrium	19
117	VIP	23
118	VIP Air	23
119	XT	11
120	Z FLX	7
121	Zero Hård	14
122	Zero Medium	14
123	Zero Megasoft	14
124	Zero Soft	14
125	Standard	2
126	SP-Line	5
127	Flex	5
128	Signature Line	5
129	D-Line	5
130	RDGA-Line	5
131	Neutron	4
132	Proton	4
133	Standard	13
137	X-Link	21
138	X-Link Firm	21
139	X-Link Soft	21
140	Granite X-Link	21
141	Glow DX	11
142	Glow Champion	11
143	X-Line	6
144	SG-Line	6
145	Glo FLX	7
146	Cryztal Z	7
\.

SELECT pg_catalog.setval('r_muovi_id_seq', 146, true);

COPY r_vari (id, vari) FROM stdin;
1	ANY
2	Harmaa
3	Keltainen
4	Lila
5	Lime
6	Monivärinen
7	Musta
8	Oranssi
9	Pinkki
10	Punainen
11	Ruskea
12	Sininen
13	Turkoosi
14	Valkoinen
15	Vihreä
\.

SELECT pg_catalog.setval('r_vari_id_seq', 16, false);

COPY kiekot (id, member_id, mold_id, muovi_id, vari_id, kuva, paino, kunto, hohto, spessu, dyed, swirly, tussit, myynnissa, hinta, muuta, loytokiekko, itb) FROM stdin;
3	2	844	24	9	kiekko_1490124214.jpg	168	10	f	t	f	f	1	t	40		f	f
5	2	669	62	6	kiekko_1490125508.jpg	170	10	f	f	t	f	1	f	0		f	f
6	2	747	58	14	kiekko_1490126045.jpg	176	10	f	f	t	f	1	f	0	NBDG Talvisarja 2017 Osakilpailu 6/8 Ristikivi	f	f
7	2	715	115	14	kiekko_1490127361.jpg	174	9	f	t	f	f	2	f	0	Amsterdam Challenge 2016 fundraiser	f	f
9	2	694	115	3	kiekko_1490128320.jpg	177	9	f	f	f	f	2	f	0		f	f
10	2	596	25	14	kiekko_1490160028.jpg	175	9	t	f	f	f	2	f	0		f	f
11	2	845	67	12	kiekko_1490160341.jpg	0	8	f	f	f	f	2	f	0		f	f
12	2	846	103	15	kiekko_1490162327.jpg	175	9	f	t	f	t	2	f	0		f	f
13	2	669	109	10	kiekko_1490162958.jpg	170	8	f	f	t	f	2	f	0		f	f
14	2	757	82	8	kiekko_1490165106.jpg	171	9	f	f	f	f	2	f	0		f	f
16	2	641	109	8	kiekko_1490174088.jpg	171	8	f	f	t	f	3	f	0	UV tuloste	f	f
17	2	570	109	7	kiekko_1490175274.jpg	168	8	f	f	t	f	2	f	0	3 rivinen AJ	f	f
18	2	570	109	6	kiekko_1490175562.jpg	170	8	f	f	t	f	2	f	0	Bottom Stamp	f	f
21	2	603	109	9	kiekko_1490186064.jpg	162	8	f	f	t	f	6	f	0		f	f
22	2	569	85	8	kiekko_1490203378.jpg	175	8	f	f	f	f	2	f	0		f	f
23	2	573	4	3	kiekko_1490203784.jpg	172	8	f	f	f	f	2	f	0		f	f
24	2	573	4	3	kiekko_1490203936.jpg	173	7	f	f	f	f	2	f	0		f	f
25	2	573	4	3	kiekko_1490204073.jpg	171	8	f	f	f	f	2	f	0		f	f
26	2	847	31	8	kiekko_1490204293.jpg	173	9	f	f	f	f	2	f	0	Tosi vakaa versio	f	f
28	2	591	62	5	kiekko_1490269934.jpg	167	9	f	f	f	f	2	f	0		f	f
39	2	592	18	9	kiekko_1490336395.jpg	138	7	f	f	f	f	2	f	0		f	f
41	2	577	25	8	kiekko_1490337853.jpg	175	8	f	f	f	f	2	f	0		f	f
42	2	673	32	8	kiekko_1490339404.jpg	175	7	f	f	f	f	4	f	0	Heijastinmaalia	f	f
43	2	850	33	8	kiekko_1490340034.jpg	174	9	f	f	t	f	2	f	0		f	f
44	2	851	102	9	kiekko_1490342825.jpg	174	8	f	t	f	f	2	f	0		f	f
46	2	595	98	8	kiekko_1490357575.jpg	169	8	f	f	f	f	2	f	0		f	f
49	2	739	82	10	kiekko_1490429776.jpg	173	10	f	f	f	f	2	f	0		f	f
50	2	853	107	7	kiekko_1490430743.jpg	143	10	f	f	f	f	1	f	0		f	f
51	2	711	115	14	kiekko_1490433242.jpg	176	8	f	f	f	f	2	f	0		f	f
56	2	855	45	6	kiekko_1490447728.jpg	173	8	f	f	f	f	2	f	0		f	f
57	2	616	62	3	kiekko_1490855202.jpg	166	8	f	f	f	f	2	f	0		f	f
58	2	634	62	12	kiekko_1490855489.jpg	175	9	f	f	f	f	2	f	0		f	f
59	2	856	90	8	kiekko_1490855729.jpg	175	9	f	f	t	f	2	f	0		f	f
61	2	608	109	6	kiekko_1490857006.jpg	166	8	f	f	t	f	2	f	0	PFN, Beast-L	f	f
62	2	576	25	12	kiekko_1490857267.jpg	175	9	f	f	f	f	2	f	0		f	f
63	2	596	25	8	kiekko_1490857914.jpg	171	8	f	f	t	f	2	f	0		f	f
64	2	571	41	14	kiekko_1490858764.jpg	177	8	t	f	t	f	2	f	0		f	f
65	2	572	41	14	kiekko_1490858913.jpg	177	9	t	f	f	f	2	f	0		f	f
66	2	688	24	14	kiekko_1490859864.jpg	172	8	t	f	f	f	2	f	0		f	f
67	2	862	41	14	kiekko_1490859996.jpg	172	9	t	f	f	f	2	f	0		f	f
68	2	854	41	13	kiekko_1490860166.jpg	174	9	t	f	f	f	2	f	0		f	f
69	2	575	4	2	kiekko_1490860283.jpg	171	7	f	f	f	f	2	f	0		f	f
71	2	679	32	3	kiekko_1490864132.jpg	176	6	f	f	f	f	1	f	0		f	f
72	2	598	25	9	kiekko_1490864401.jpg	175	9	t	f	t	f	2	f	0		f	f
73	2	611	25	9	kiekko_1490864892.jpg	168	8	f	f	f	f	2	f	0		f	f
74	2	596	109	8	kiekko_1490864975.jpg	171	8	f	f	t	f	2	f	0		f	f
75	2	569	24	8	kiekko_1490865143.jpg	175	9	f	f	f	f	2	f	0		f	f
76	2	688	24	3	kiekko_1490865294.jpg	175	8	f	f	t	f	2	f	0		f	f
77	2	806	95	12	kiekko_1490865397.jpg	175	9	f	f	t	f	2	f	0		f	f
78	2	827	30	14	kiekko_1490865638.jpg	171	10	t	f	f	f	1	f	0		f	f
79	2	843	30	14	kiekko_1490865834.jpg	175	10	f	f	f	f	1	f	0		f	f
80	2	749	58	14	kiekko_1490865993.jpg	178	9	f	f	f	f	2	f	0		f	f
82	2	675	55	15	kiekko_1490866257.jpg	171	10	f	f	f	f	1	f	0		f	f
83	2	809	81	7	kiekko_1490866389.jpg	156	10	f	f	f	f	2	t	0		f	f
85	2	832	120	5	kiekko_1490866647.jpg	173	10	f	f	f	f	1	f	0		f	f
86	2	863	41	9	kiekko_1490869133.jpg	173	9	t	f	f	f	2	f	0		f	f
87	2	864	31	3	kiekko_1490869673.jpg	173	10	f	f	f	f	1	f	0		f	f
89	2	865	72	12	kiekko_1490870188.jpg	0	8	f	f	t	f	2	f	0		f	f
90	2	866	41	14	kiekko_1490870325.jpg	0	8	f	f	f	f	4	f	0		f	f
91	2	867	41	3	kiekko_1490870453.jpg	0	8	f	f	f	f	2	f	0		f	f
92	2	626	25	3	kiekko_1490870611.jpg	0	8	f	f	f	f	2	f	0		f	f
93	2	570	62	8	kiekko_1490870671.jpg	0	7	f	f	f	f	6	f	0		f	f
94	2	579	33	10	kiekko_1490870749.jpg	0	9	f	f	f	f	2	f	0	Halpahallin merkki	f	f
95	2	596	25	5	kiekko_1490870979.jpg	0	8	f	f	t	f	2	f	0	Led + paristo kiinteänä	f	f
96	2	868	45	4	kiekko_1490871054.jpg	0	7	f	f	f	f	2	f	0		f	f
97	2	854	45	9	kiekko_1490871192.jpg	0	8	f	f	f	f	2	f	0		f	f
98	2	714	118	10	kiekko_1490875543.jpg	0	7	f	f	f	f	4	f	0	Led + paristo kiinteänä	f	f
99	2	848	16	14	kiekko_1490875691.jpg	0	7	f	f	f	f	2	f	0		f	f
100	2	848	16	14	kiekko_1490875736.jpg	0	7	f	f	f	f	2	f	0	Heijastinmaalia	f	f
101	2	710	23	7	kiekko_1490875808.jpg	0	7	f	f	f	f	2	f	0		f	f
102	2	868	41	14	kiekko_1490875885.jpg	0	8	t	f	f	f	2	f	0		f	f
103	2	866	41	14	kiekko_1490876035.jpg	0	8	t	f	f	f	2	f	0		f	f
104	2	869	41	5	kiekko_1490876095.jpg	0	8	f	f	f	f	2	f	0	Led + paristo kiinteänä	f	f
105	2	592	25	3	kiekko_1490876309.jpg	0	7	f	f	f	f	3	f	0	Led + paristo kiinteänä	f	f
70	2	569	103	6	kiekko_1490860914.jpg	171	8	f	f	t	f	2	f	0		f	t
15	2	598	109	14	kiekko_1490172114.jpg	172	8	f	f	t	f	2	f	0		f	t
106	2	575	7	6	kiekko_1490876396.jpg	0	8	f	f	f	f	2	f	0	Led + paristo kiinteänä	f	f
107	2	573	7	9	kiekko_1490876455.jpg	0	8	t	f	t	f	2	f	0		f	f
108	2	608	109	3	kiekko_1490876577.jpg	0	7	f	f	t	f	4	f	0	HIO, Tali #2, Beast-L	f	f
109	2	571	41	10	kiekko_1490876684.jpg	0	8	f	f	t	f	4	f	0	HIO, Söderkulla #2	f	f
110	2	709	22	9	kiekko_1490876979.jpg	0	8	f	f	f	f	2	f	0		f	f
111	2	572	41	10	kiekko_1490877211.jpg	0	8	f	f	f	f	2	f	0		f	f
112	2	870	69	14	kiekko_1490877391.jpg	0	8	f	f	f	f	2	f	0		f	f
113	2	854	41	8	kiekko_1490877523.jpg	0	9	t	f	f	f	2	f	0		f	f
114	2	871	68	14	kiekko_1490877641.jpg	0	10	f	f	f	f	1	f	0		f	f
115	2	691	103	3	kiekko_1490877992.jpg	169	10	f	f	f	f	1	f	0		f	f
116	2	598	25	10	kiekko_1490878156.jpg	0	8	f	f	f	f	2	f	0		f	f
117	2	824	41	5	kiekko_1490878523.jpg	0	10	f	t	f	f	1	f	0		f	f
118	2	608	25	12	kiekko_1490878803.jpg	0	8	f	f	f	f	1	f	0	Beast-x mold	f	f
119	2	806	81	14	kiekko_1490882823.jpg	0	7	f	f	f	f	3	f	0		f	f
120	2	757	121	5	kiekko_1490882878.jpg	0	8	f	f	f	f	2	f	0		f	f
122	2	849	105	2	kiekko_1490883216.jpg	175	8	f	f	t	f	2	f	0		f	f
123	2	794	81	6	kiekko_1490883316.jpg	0	8	f	f	t	f	2	f	0		f	f
125	2	738	53	6	kiekko_1491582326.jpg	169	10	f	f	t	f	2	f	0		f	f
126	2	733	58	6	kiekko_1491594715.jpg	175	10	f	f	t	f	2	f	0		f	f
127	2	598	109	3	kiekko_1491655352.jpg	175	8	f	f	t	f	4	f	0		f	f
190	1	854	120	3	Bizhop-190	173	8	f	f	f	f	2	f	0		f	f
204	1	571	41	15	Bizhop-204	176	8	f	f	f	f	3	f	0	Siltamäki #4	f	f
19	2	691	103	6	kiekko_1490181176.jpg	167	9	f	t	f	t	2	f	45		f	f
192	1	971	4	9	Bizhop-192	175	6	f	f	f	f	2	f	0		t	f
221	1	610	25	8	Bizhop-221	175	8	f	f	f	f	2	f	0		f	t
218	1	688	24	10	Bizhop-218	170	7	f	f	f	f	2	f	0		f	t
205	1	571	41	3	Bizhop-205	178	9	f	f	f	f	2	f	0	Big Z	f	f
183	1	577	25	12	Bizhop-183	175	7	f	f	f	f	2	f	0		f	f
195	1	569	24	10	Bizhop-195	175	9	f	f	f	f	2	f	0		f	f
185	1	584	90	3	Bizhop-185	178	8	f	f	t	f	2	f	0		f	f
186	1	587	62	10	Bizhop-186	175	9	f	t	f	f	2	f	0	First run	f	f
196	1	569	24	8	Bizhop-196	175	9	f	f	f	f	3	f	0		f	f
187	1	626	35	8	Bizhop-187	174	5	f	f	f	f	3	f	0		t	f
197	1	569	55	10	Bizhop-197	172	9	f	t	f	f	2	f	0	Lizotte Flying Circus	f	f
198	1	691	103	5	Bizhop-198	167	8	f	f	f	f	2	f	0		f	f
207	1	667	35	3	Bizhop-207	174	8	f	f	f	f	2	f	0		f	f
121	2	873	96	4	kiekko_1490883139.jpg	0	8	f	f	f	f	3	f	0	hävinnyt kivikon #1	f	f
199	1	691	24	8	Bizhop-199	166	8	f	f	f	f	2	f	0		f	f
200	1	587	109	8	Bizhop-200	167	8	f	f	f	f	2	f	0		f	f
191	1	857	16	10	Bizhop-191	171	9	f	f	f	f	1	f	0		f	f
202	1	571	41	15	Bizhop-202	180	8	f	f	f	f	2	f	0		f	f
176	2	587	109	8	pekkanyk-176	174	10	f	f	f	f	1	f	0		f	f
211	1	971	7	3	Bizhop-211	183	8	f	t	f	f	2	f	0	Kiinteät ledit	f	f
159	16	679	103	3	Hormoni-159	176	10	f	t	f	f	1	f	0	Simon Lizotte Fantasy Doubles Tour	f	f
182	1	664	119	3	Bizhop-182	175	8	f	f	f	f	2	t	8		f	f
194	1	688	24	10	Bizhop-194	169	10	f	f	f	f	1	f	0		f	f
217	1	688	24	8	Bizhop-217	175	8	f	f	f	f	4	f	0		f	t
220	1	577	142	14	Bizhop-220	175	8	t	t	f	f	2	f	0	Nate Sexton tour series	f	t
164	1	571	56	14	Bizhop-164	180	10	t	t	f	f	1	f	20	Halloween	f	f
222	1	610	109	10	Bizhop-222	167	8	f	f	f	f	2	f	0		f	t
213	1	866	56	14	Bizhop-213	173	8	t	f	f	f	1	f	0		f	f
223	1	1048	103	8	Bizhop-223	167	9	f	f	f	f	2	f	0		f	t
206	1	571	45	15	Bizhop-206	172	10	f	f	f	f	1	f	0		f	f
224	1	569	24	15	Bizhop-224	171	8	f	t	f	f	2	f	0		f	t
188	1	638	35	3	Bizhop-188	175	9	f	f	f	f	1	f	0	X-out	f	f
225	1	569	24	8	Bizhop-225	168	8	f	f	f	f	2	f	0		f	t
189	1	669	35	15	Bizhop-189	112	8	f	f	f	f	1	f	0		f	f
201	1	711	115	14	Bizhop-201	177	9	f	t	t	f	1	f	0		f	f
208	1	669	119	10	Bizhop-208	171	10	f	f	f	f	1	f	0		f	f
203	1	642	25	7	Bizhop-203	177	8	f	f	f	f	1	f	0		f	f
214	1	888	56	14	Bizhop-214	173	8	t	f	f	f	1	f	0		f	f
226	1	569	103	8	Bizhop-226	172	8	f	f	f	f	2	f	0		f	t
227	1	569	55	10	Bizhop-227	171	8	f	f	f	f	2	f	0		f	t
228	1	572	41	15	Bizhop-228	178	8	f	f	f	f	2	f	0		f	t
229	1	571	145	15	Bizhop-229	180	9	t	t	f	f	2	f	0	2015 Ledgestone Edition	f	t
230	1	571	120	15	Bizhop-230	180	8	f	f	f	f	2	f	0		f	t
231	1	642	119	7	Bizhop-231	175	8	f	f	f	f	2	f	0		f	t
232	1	854	146	3	Bizhop-232	175	8	f	t	f	f	2	f	0	Estonian Edition	f	t
233	1	1050	109	3	Bizhop-233	173	8	f	f	f	f	2	f	0		f	t
234	1	1050	35	8	Bizhop-234	172	8	f	f	f	f	2	f	0		f	t
235	1	659	119	8	Bizhop-235	175	8	f	f	f	f	2	f	0		f	t
236	1	669	119	8	Bizhop-236	173	8	f	f	f	f	2	f	0		f	t
237	1	669	119	10	Bizhop-237	173	8	f	f	f	f	2	f	0		f	t
60	2	857	90	10	kiekko_1490856860.jpg	175	8	f	f	t	f	1	f	0	Led + paristo kiinteänä	f	f
193	1	809	81	7	Bizhop-193	156	10	f	t	f	f	1	f	0	PDGA	f	f
160	28	659	119	9	Uusi28-160	175	10	f	f	f	f	1	f	0		f	f
219	1	687	103	10	Bizhop-219	170	8	f	f	f	f	1	f	0		f	t
210	1	571	56	14	Bizhop-210	177	8	t	f	f	f	1	f	0		f	f
209	1	669	141	14	Bizhop-209	175	8	t	f	f	f	1	f	0		f	f
212	1	611	142	14	Bizhop-212	175	8	t	t	f	f	1	f	0	Hypno huk	f	f
216	1	869	56	14	Bizhop-216	173	10	t	f	f	f	1	f	0		f	f
\.

SELECT pg_catalog.setval('kiekot_id_seq', 237, true);

COPY wanted (wanted_id, member_id, mold_id, muovi_id, vari, paino, muuta) FROM stdin;
2	27	596	25	Oranssi	168-171	
8	2	854	31	ANY	170-175	Halvalla
\.

SELECT pg_catalog.setval('wanted_wanted_id_seq', 9, false);
