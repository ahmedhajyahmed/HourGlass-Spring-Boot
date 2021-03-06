package com.Project.Hourglass.Controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.Project.Hourglass.Repositories.ClientRepository;
import com.Project.Hourglass.Repositories.CoachRepository;
import com.Project.Hourglass.Repositories.DayprogramRepository;
import com.Project.Hourglass.Repositories.MealRepository;
import com.Project.Hourglass.Repositories.NutritionalprogramRepository;
import com.Project.Hourglass.Repositories.SportsprogramRepository;
import com.Project.Hourglass.Repositories.WeightlossprogramRepository;
import com.Project.Hourglass.Repositories.WorkoutRepository;
import com.Project.Hourglass.model.Audiance;
import com.Project.Hourglass.model.Client;
import com.Project.Hourglass.model.Coach;
import com.Project.Hourglass.model.Dayprogram;
import com.Project.Hourglass.model.Meal;
import com.Project.Hourglass.model.Nutritionalprogram;
import com.Project.Hourglass.model.Sportsprogram;
import com.Project.Hourglass.model.User;
import com.Project.Hourglass.model.Weightlossprogram;
import com.Project.Hourglass.model.Workout;

import Pogo.NutritionalPogo;
import Pogo.ProgramPogo;
import Pogo.SportsPogo;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@CrossOrigin
@RestController
@RequestMapping({"WeightLossProgram"})
public class WeightlossprogramController {
	@Autowired
	public WeightlossprogramRepository wlpRepo;
	@Autowired
	public ClientRepository clientRepo;
	@Autowired 
	public CoachRepository coachRepo;
	@Autowired
	public MealRepository mealRepo;
	@Autowired
	public WorkoutRepository workoutRepo;
	@Autowired
	public NutritionalprogramRepository npRepo;
	@Autowired
	public SportsprogramRepository spRepo;
	@Autowired
	public DayprogramRepository dayProgramRepo;
	@Autowired
	public NutritionalprogramRepository nutRepo;
	@Autowired
	public SportsprogramRepository sportRepo;
	
	//lezemna f kol return n specifiw l objet li bech nraj3ouh
	@GetMapping("")
	public List<Weightlossprogram> getAllprogram(){
		return wlpRepo.findAll();
	}
	@GetMapping("/{id}")
	public Weightlossprogram getProgram(@PathVariable Long id) {
		
		return wlpRepo.findById(id).get();
	}

	@GetMapping("byCoach/{id}")
	public List<Weightlossprogram> getProgramByCoachId(@PathVariable Long id){
		return wlpRepo.findProgramByCoachId(id);
	}
	@GetMapping("byClient/{id}")
	public Weightlossprogram getProgramByClientId(@PathVariable Long id){
		return wlpRepo.findProgramByClientId(id);
	}

	@GetMapping("old/byClient/{id}")
	public List<Weightlossprogram> getProgramsByClientId(@PathVariable Long id) {
		return wlpRepo.findOldProgramsByClientId(id);
	}

	@PutMapping("/rateProgram/{id}")
	public Weightlossprogram rateProgram(@RequestBody Weightlossprogram newwlp, @PathVariable long id)
	{
		return wlpRepo.findById(id).map(program ->{
				program.setRating(newwlp.getRating());
				return wlpRepo.save(program);
		}).orElseGet(() -> {
			newwlp.setId(id);
			return wlpRepo.save(newwlp);
		});
	}
	@PostMapping("/{coachId}")
	public Weightlossprogram SaveProgram(@PathVariable Long coachId,@RequestBody ProgramPogo p) {

		Client client=clientRepo.findById(Long.valueOf(999)).get();
		Coach coach=coachRepo.findById(coachId).get();
		Audiance audiance=new Audiance(p.audiance.sex,p.audiance.height,p.audiance.objectiveWeight,p.audiance.frame,p.audiance.fatStorage,p.audiance.silhouette,p.audiance.overWeightCause);
		Weightlossprogram programToSave=new Weightlossprogram(p.description,LocalDate.now(),p.duration,p.rating,p.backgroundImage,p.objectifs,client,coach,audiance,p.name, p.price);
		wlpRepo.save(programToSave);
		Weightlossprogram wlp=wlpRepo.findByName(p.name).get();
		for(NutritionalPogo np:p.nutritionalPrograms) {
			Set<Meal> meals=new HashSet<Meal>();
			for(String m:np.meals) {
				Meal meal=mealRepo.findByName(m).get();
				meals.add(meal);
			}
			Nutritionalprogram npToSave=new Nutritionalprogram(np.mealsNumber,meals,np.day,np.description,wlp, np.name);
			npRepo.save(npToSave);
			
		}
		
		for(SportsPogo wp:p.sportsPrograms) {
			Set<Workout> workouts=new HashSet<Workout>();
			for(String m:wp.workouts) {
				Workout workout=workoutRepo.findByName(m).get();
				workouts.add(workout);
			}
			Sportsprogram spToSave=new Sportsprogram(workouts,wp.day,wp.description,wlp,wp.name);
			spRepo.save(spToSave);
		}
		return programToSave;
	}
	@GetMapping("affecter/{clientId}/{programId}")
	public Weightlossprogram affecterProgramme(@PathVariable Long clientId,@PathVariable Long programId ) {
		Client client=clientRepo.findById(clientId).get();
		Weightlossprogram program= wlpRepo.findById(programId).get();
		Weightlossprogram newProgram=new Weightlossprogram(program.getDescription(),LocalDate.now().plusDays(program.getDuration()),program.getDuration(),program.getRating(),program.getBackgroundImage(),program.getObjectifs(),null,program.getCoach(),program.getAudiance(),program.getName(),program.getPrice());
		newProgram.setClient(client);
		wlpRepo.save(newProgram);
		newProgram=wlpRepo.findProgramByClientId(clientId);
		List<Nutritionalprogram> nutPrograms=nutRepo.findNutritionalprogamByWeightlossprogramId(programId);
		List<Sportsprogram> sportsPrograms=sportRepo.findSportsprogramByWeightlossprogramId(programId);
		System.out.println(programId + "kkkk"+ nutPrograms.size());

		for(Nutritionalprogram p:nutPrograms) {
			Nutritionalprogram newNut=new Nutritionalprogram(p.getMealsNumber(),new HashSet<Meal>(p.getMeals()),LocalDate.now().plusDays(Integer.parseInt(p.getDay())).toString(),p.getDescription(),newProgram,p.getName());
			newNut.setWeightLossProgram(newProgram);
			
			nutRepo.save(newNut);
			//p.getMeals().forEach((x) -> {x.getNutritionalPrograms().add(newNut);
			//mealRepo.save(x);
			//});
			}
		
		for(Sportsprogram p:sportsPrograms) {
			Sportsprogram newSp=new Sportsprogram(LocalDate.now().plusDays(Integer.parseInt(p.getDay())).toString(),p.getDescription(),new HashSet<Workout>(p.getWorkouts()),newProgram,p.getName());
			System.out.println(newSp.getDay());
			System.out.println(p.getDay());
			
			sportRepo.save(newSp);
			
		}
		return program;
		
		
	}
}
