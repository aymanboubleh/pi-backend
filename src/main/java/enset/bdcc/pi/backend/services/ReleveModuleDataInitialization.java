package enset.bdcc.pi.backend.services;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import enset.bdcc.pi.backend.dao.*;
import enset.bdcc.pi.backend.entities.*;
import enset.bdcc.pi.backend.entities.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.beans.Transient;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class ReleveModuleDataInitialization {
    List<Element> semestre1Elements = new ArrayList<>();
    List<Element> semestre2Elements = new ArrayList<>();
    List<Element> semestre3Elements = new ArrayList<>();
    List<Element> semestre4Elements = new ArrayList<>();
    List<Filiere> filiereList = new ArrayList<>();
    List<Diplome> diplomeList = new ArrayList<>();
    ArrayList<Etudiant> etudiantArrayList = new ArrayList<>();
    List<Session> sessionList = new ArrayList<>();
    @Autowired
    PasswordEncoder passwordEncoder;
    String libele_year = "";
    @Autowired
    private AttestationScolariteRepository attestationRepository;
    @Autowired
    private DemandeReleveRepository demandeReleveRepository;
    @Autowired
    private EtudiantSessionRepository etudiantSessionRepository;
    @Autowired
    private SemestreFiliereRepository semestreFiliereRepository;
    @Autowired
    private ElementRepository elementRepository;
    @Autowired
    private ModuleRepository moduleRepository;
    @Autowired
    private FiliereRepository filiereRepository;
    @Autowired
    private EtudiantRepository etudiantRepository;
    @Autowired
    private SemestreEtudiantRepository semestreEtudiantRepository;
    @Autowired
    private DiplomeRepository diplomeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SessionRepository sessionRepository;
    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddlAuto;

    @EventListener(ApplicationReadyEvent.class)
    public void initData() {
        if (ddlAuto.equals("update")) return;
        preloadUsers();
        preloadElemnts();
        preloadDiplomes();
        preloadFiliere1();
        preloadFiliere2();
        System.out.println("Date :" + Calendar.getInstance().get(Calendar.YEAR));
//        initAttestations(Calendar.getInstance().get(Calendar.YEAR));
//        preloadSession();
//        preloadDemandeReleves();
    }

    @Transactional
    public void preloadDemandeReleves() {

//        List<DemandeReleve> demandeReleveList = new ArrayList<>();
//        List<Etudiant> etudiantList =  etudiantRepository.findAll();
//        for (Etudiant etudiant : etudiantList) {
//
//            Etudiant etud = etudiantRepository.getOne(etudiant.getId());
//            SemestreEtudiant semestreEtudiant = etud.getSemestreEtudiants().get(0);
//            DemandeReleve demandeReleve = new DemandeReleve();
//            demandeReleveList.add(demandeReleve);
//            demandeReleve.setSemestreEtudiant(semestreEtudiant);
//        }
//            demandeReleveRepository.saveAll(demandeReleveList);

    }

    @Transactional
    public void initAttestations(int year) {

        Session session = sessionRepository.findById((long) 45).get();
        System.out.println("Annee session : " + session.getAnnee());
        if (session.getAnnee() == year) {
            etudiantSessionRepository.findAll().forEach(etudse -> {
                Attestation_scolarite attestation = new Attestation_scolarite();
                attestation.setCodeEtudiant(etudse.getEtudiant().getId());
                attestation.setNomComplet(etudse.getEtudiant().getNom() + " " + etudse.getEtudiant().getPrenom());
                attestation.setCin(etudse.getEtudiant().getCin());
                attestation.setCne(etudse.getEtudiant().getCne());
                try {
                    attestation.setDate_naissance(new SimpleDateFormat("yyyy-MM-dd").parse(etudse.getEtudiant().getDate_naissance().toString()));
                    attestation.setVille_naissance(etudse.getEtudiant().getVille_naissance());
                    attestation.setAnnee_session(etudse.getSession().getAnnee() + "/" + Integer.toString(etudse.getSession().getAnnee() + 1));
                    switch (etudse.getSession().getAnnee_courante()) {
                        case 1:
                            libele_year = " ère année";
                            break;
                        case 2:
                            libele_year = " ème année";
                            break;
                        default:
                            libele_year = " ème année";
                            break;
                    }
                    attestation.setAnnee_univers(Integer.toString(etudse.getSession().getAnnee_courante()) + libele_year + " " + etudse.getSession().getFiliere().getDiplome().getDescription() + " : " + etudse.getSession().getFiliere().getDescription());
                    attestation.setType_diplome("Fl. " + etudse.getSession().getFiliere().getDiplome().getDescription().replace("Cycle ", "") + " ." + etudse.getSession().getFiliere().getLibelle());
                    attestation.setEtudiant(etudse.getEtudiant());
                    attestationRepository.save(attestation);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });
        } else {
            System.out.println("Impossible !!");
        }
    }

    @Transactional
    public void preloadDiplomes() {

        diplomeList.add(new Diplome("cycle_ing", "Cycle d'ingénieur", 3));
        diplomeList.add(new Diplome("cycle_mast", "Cycle Master", 2));
        diplomeList.add(new Diplome("cycle_dut", "Cycle DUT", 2));
        diplomeList.add(new Diplome("cycle_lic", "Cycle Licence", 1));
        diplomeRepository.saveAll(diplomeList);

    }

//    @Bean
//    public BCryptPasswordEncoder getPasswordEncoder() {
//        if (passwordEncoder == null) return new BCryptPasswordEncoder();
//        return passwordEncoder;
//    }

    @Transactional
    public void preloadSession() {
        List<SemestreFiliere> semestreFiliereList = new ArrayList<>(filiereList.get(0).getSemestreFilieres());
        List<SemestreEtudiant> semestreEtudiants = new ArrayList<>();

        //TOUT D'abord on crée la session
        Session session = new Session(2004, filiereList.get(0));
        //On recopie les modules et elements de la filieres, pour avoir ses propres modules
        session.getFiliere().getSemestreFilieres().forEach(semestreFiliere -> {
            SemestreFiliere semestreSession = new SemestreFiliere(semestreFiliere.getNumero(), semestreFiliere.isDone(), session);
            semestreFiliere.getModules().forEach(sfModule -> { //sf = semestreFiliere, ss = semestreSesison
//                System.out.println("HERE IS A FUCKING SF" + sfModule.getLibelle());
                Module ssModule = new Module(sfModule.getLibelle());
                ssModule.setSemestreFiliere(semestreSession);
                sfModule.getElements().forEach(element -> {
                    ssModule.getElements().add(element);
                });
                semestreSession.getModules().add(ssModule);
            });
            session.getSemestreFilieres().add(semestreSession);
        });
        sessionRepository.save(session);
        //Ensuite on crée les semestres des étudiants et on les lient avec les modules de la session
        session.getSemestreFilieres().forEach(semestreSession -> {
            etudiantArrayList.forEach(etudiant -> {
                SemestreEtudiant semestreEtudiant = new SemestreEtudiant(etudiant, session, semestreSession.getNumero(), false);
                semestreSession.getModules().forEach(ssModule -> {
                    NoteModule noteModule = new NoteModule(ssModule, semestreEtudiant);
                    ssModule.getElements().forEach(element -> {
                        NoteElementModule noteElementModule = new NoteElementModule(noteModule, element);
                        noteModule.getNoteElementModules().add(noteElementModule);
                    });
                    semestreEtudiant.getNoteModules().add(noteModule);
                });
                etudiant.getSemestreEtudiants().add(semestreEtudiant);
                session.getSemestreEtudiants().add(semestreEtudiant);
            });
        });

        sessionRepository.save(session);
        sessionList.add(session);
        //Ensuite on crée les sessions des étudiants
        List<EtudiantSession> etudiantSessionList = new ArrayList<>();
        for (Etudiant etudiant : etudiantArrayList) {
            etudiantSessionList.add(new EtudiantSession(etudiant, session));
        }
        etudiantSessionRepository.saveAll(etudiantSessionList);
    }

    @Transactional
    public void preloadUsers() {
        etudiantArrayList.add(new Etudiant("MA137551", "Zakaria", "Chadli", passwordEncoder.encode("123"), "15132215864", "homme", LocalDate.of(1997, 5, 20), "Kenitra", "zakaria.chadli@gmail.com", "dickhead"));
        etudiantArrayList.add(new Etudiant("RP137552", "Hamza", "Gueddi", passwordEncoder.encode("123"), "1525486868788", "homme", LocalDate.of(1997, 5, 20), "Salé", "hamza.gueddi@gmail.com", "homo"));
        etudiantArrayList.add(new Etudiant("CA137553", "Yassine", "Faiq", passwordEncoder.encode("123"), "1525486868788", "homme", LocalDate.of(1997, 5, 20), "Laayoune", "yassine.faiq@gmail.com", "simp"));
        etudiantRepository.saveAll(etudiantArrayList);
        User user = new User();
        user.setEmail("admin");
        user.setPassword("admin");
        user.setNom("admin");
        user.setPrenom("admin");
        user.setSexe("Male");
        user.setRole(User.ROLE_ADMIN);
        userRepository.save(user);

    }

    public void preloadElemnts() {
        semestre1Elements.add(new Element("Analyse Numérique 1"));
        semestre1Elements.add(new Element("Logique et Algèbre Linéaire"));
        semestre1Elements.add(new Element("Probabilité"));
        semestre1Elements.add(new Element("Recherche Operationnelle"));
        semestre1Elements.add(new Element("Algorithmique"));
        semestre1Elements.add(new Element("Programmation en langage C"));
        semestre1Elements.add(new Element("Introduction aux bases de données"));
        semestre1Elements.add(new Element("SQL et SGBD"));
        semestre1Elements.add(new Element("Architecture des ordinateurs et assembleur"));
        semestre1Elements.add(new Element("Techniques de base pour les réseaux"));
        semestre1Elements.add(new Element("Economie générale"));
        semestre1Elements.add(new Element("Environnement socio-économique et institutionnel"));
        semestre1Elements.add(new Element("Anglais 1"));
        semestre1Elements.add(new Element("Techniques de communication en langue française 1"));
        elementRepository.saveAll(semestre1Elements);
        semestre1Elements.add(new Element("Analyse Numérique 2"));
        semestre2Elements.add(new Element("Statistiques"));
        semestre2Elements.add(new Element("Programmation fonctionnelle : concepts et outils"));
        semestre2Elements.add(new Element("Structures de données"));
        semestre2Elements.add(new Element("Conception et programmation orientée objet avec C++"));
        semestre2Elements.add(new Element("Projet programmation orientée objet avec C++"));
        semestre2Elements.add(new Element("Développement web"));
        semestre2Elements.add(new Element("Projet Développement web"));
        semestre2Elements.add(new Element("Systèmes d’exploitation Windows/Unix/Linux"));
        semestre2Elements.add(new Element("Théorie des systèmes d’exploitation"));
        semestre2Elements.add(new Element("Projet personnel 1"));
        semestre2Elements.add(new Element("Comptabilité générale"));
        semestre2Elements.add(new Element("Gestion"));
        semestre2Elements.add(new Element("Anglais 2"));
        semestre2Elements.add(new Element("Techniques de communication en langue française 2"));
        elementRepository.saveAll(semestre2Elements);
    }

    @Transactional
    public void preloadFiliere1() {   //Filieres  + Modules + Elements

        List<Module> moduleList1 = new ArrayList<>();
        List<Module> moduleList2 = new ArrayList<>();
        Module m1 = new Module("Mathematique Appliquée 1");
        Module m2 = new Module("Mathematique Appliquée 2");
        Module m3 = new Module("Techniques de programmation");
        Module m4 = new Module("Bases de données");
        Module m5 = new Module("Technologies");

        m1.getElements().add(semestre1Elements.get(0));
        m1.getElements().add(semestre1Elements.get(1));
        m2.getElements().add(semestre1Elements.get(2));
        m2.getElements().add(semestre1Elements.get(3));
        m3.getElements().add(semestre1Elements.get(4));
        m3.getElements().add(semestre1Elements.get(5));
        m4.getElements().add(semestre1Elements.get(6));
        m4.getElements().add(semestre1Elements.get(7));
        m5.getElements().add(semestre1Elements.get(8));
        moduleList1.add(m1);
        moduleList1.add(m2);
        moduleList2.add(m3);
        moduleList2.add(m4);
        moduleList2.add(m5);
        Filiere filiere = new Filiere("GLSID", "Génie Logiciel Système Informatique Distribuées", 2, diplomeRepository.getByLibelleContains("cycle_ing"));

        filiereList.add(filiere);
        SemestreFiliere semestre1 = new SemestreFiliere(1);
        for (Module module1 : moduleList1) {
            module1.setSemestreFiliere(semestre1);
        }
        semestre1.getModules().addAll(moduleList1);
        semestre1.setFiliere(filiere);
        filiere.getSemestreFilieres().add(semestre1);
        SemestreFiliere semestre2 = new SemestreFiliere(2);
        for (Module module : moduleList2) {
            module.setSemestreFiliere(semestre2);
        }
        semestre2.getModules().addAll(moduleList2);
        semestre2.setFiliere(filiere);
        filiere.getSemestreFilieres().add(semestre2);
        filiereRepository.save(filiere);

    }

    public void preloadFiliere2() {
        List<Module> moduleList1 = new ArrayList<>();
        List<Module> moduleList2 = new ArrayList<>();
        Module m1 = new Module("Mathematique Appliquée 3");
        Module m2 = new Module("Structures de dennées et programmation Fonctionnelles");
        Module m3 = new Module("Programmation oriéntée objet");
        Module m4 = new Module("Technologies web");
        Module m5 = new Module("Systeme d'exploitation");

        m1.getElements().add(semestre1Elements.get(0));
        m1.getElements().add(semestre1Elements.get(1));
        m2.getElements().add(semestre1Elements.get(2));
        m2.getElements().add(semestre1Elements.get(3));
        m3.getElements().add(semestre1Elements.get(4));
        m3.getElements().add(semestre1Elements.get(5));
        m4.getElements().add(semestre1Elements.get(6));
        m4.getElements().add(semestre1Elements.get(7));
        m5.getElements().add(semestre1Elements.get(8));
        moduleList1.add(m1);
        moduleList1.add(m2);
        moduleList2.add(m3);
        moduleList2.add(m4);
        moduleList2.add(m5);
        Filiere filiere = new Filiere("II-BDCC", "Ingénierie Informatique - Big Data & Cloud Computing", 2, diplomeRepository.getByLibelleContains("cycle_ing"));
        filiereList.add(filiere);

        SemestreFiliere semestre1 = new SemestreFiliere(1);
        for (Module module1 : moduleList1) {
            module1.setSemestreFiliere(semestre1);
        }
        semestre1.getModules().addAll(moduleList1);
        semestre1.setFiliere(filiere);
        filiere.getSemestreFilieres().add(semestre1);
        SemestreFiliere semestre2 = new SemestreFiliere(2);
        for (Module module : moduleList2) {
            module.setSemestreFiliere(semestre2);
        }
        semestre2.getModules().addAll(moduleList2);
        semestre2.setFiliere(filiere);
        filiere.getSemestreFilieres().add(semestre2);
        filiereRepository.save(filiere);

    }

}
