package de.mpg.mpiinf.ag1.kpm.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.mpg.mpiinf.ag1.kpm.Parameters;
import de.mpg.mpiinf.ag1.kpm.Program;
import de.mpg.mpiinf.ag1.kpm.main.Main;
import de.mpg.mpiinf.ag1.kpm.utils.OutputSettings;
import de.mpg.mpiinf.ag1.kpm.utils.Separator;
import dk.sdu.kpm.Algo;
import dk.sdu.kpm.Combine;
import dk.sdu.kpm.Heuristic;
import dk.sdu.kpm.KPMSettings;
import dk.sdu.kpm.algo.glone.LocalSearch;
import dk.sdu.kpm.algo.glone.RhoDecay;
import dk.sdu.kpm.perturbation.PerturbationService;
import dk.sdu.kpm.perturbation.IPerturbation.PerturbationTags;
import dk.sdu.kpm.utils.Comparison;
import dk.sdu.kpm.utils.Comparator;

public class ArgsParametersParser {

    private volatile KPMSettings kpmSettings;
    
	public ArgsParametersParser(KPMSettings settings) {
    	this.kpmSettings = settings;
	}
	
	public Parameters parse(String[] args, Parameters params) throws Exception {
		/*String propsFileName = "kpm.properties";
		parsePropertiesFile(propsFileName);*/
        HashMap<String, String> id2path = new HashMap<String, String>();
        HashMap<String, Integer> id2param = new HashMap<String, Integer>();
		String algorithm = "GREEDY";
		String strategy = "INES";
		final List<String> algoList = new ArrayList<String>(3);
		algoList.add("GREEDY");
		algoList.add("ACO");
		algoList.add("OPTIMAL");
        algoList.add("FDR");
		final List<String> strategyList = new ArrayList<String>(2);
		strategyList.add("INES");
		strategyList.add("GLONE");
        strategyList.add("FDR");

		for (String arg : args) {
			String[] options = arg.split("=");
			// INPUT FILES
			if (options[0].equals("-graphFile")) {
				params.GRAPH_FILE = options[1];                
			} else if (options[0].startsWith("-isDirected")) {
				params.IS_GRAPH_DIRECTED = Boolean.parseBoolean(options[1]);
			} else if (options[0].startsWith("-matrix")) {
				String id = "L" + options[0].substring(7);
				String path = options[1];

				String internalID = kpmSettings.externalToInternalIDManager.getOrCreateInternalIdentifier(id);
				id2path.put(internalID, path);
			} else if (options[0].equals("-gfHeader")) {
				params.GRAPH_FILE_HAS_HEADER = Boolean.parseBoolean(options[1]);
			} else if (options[0].equals("-gfSep")) {
				params.GRAPH_FILE_SEPARATOR = Separator.valueOf(options[1]);
			} else if (options[0].equals("-datasetsFile")) {
				params.DATASETS_FILE = options[1];
			} else if (options[0].equals("-dfHeader")) {
				params.DATASETS_FILE_HAS_HEADER = Boolean.parseBoolean(options[1]);
			} else if (options[0].equals("-dfSep")) {
				params.DATASETS_FILE_SEPARATOR = Separator.valueOf(options[1]);
			} else if (options[0].equals("-positiveFile")) {
				params.POSITIVE_FILE = options[1];
			} else if (options[0].equals("-negativeFile")) {
				params.NEGATIVE_FILE = options[1];
			} else if (options[0].equals("-mfHeader")) {
				kpmSettings.MATRIX_FILES_HAVE_HEADER = true;
			} else if (options[0].equals("-mfSep")) {
				params.MATRIX_FILES_SEPARATOR = Separator.valueOf(options[1]);
				// OUTPUT FILES 
			} else if (options[0].equals("-fileExt")) {
				params.FILE_EXTENSION = options[1];
			}
			else if (options[0].equals("-summaryFile")) {
				OutputSettings.SUMMARY_FILE = options[1];
			} else if (options[0].equals("-pathwaysFile")) {
				OutputSettings.PATHWAYS_FILE = options[1];
			} else if (options[0].equals("-pathwaysStatsFile")) {
				OutputSettings.PATHWAYS_STATS_FILE = options[1];
			} else if (options[0].equals("-geneStatsFile")) {
				OutputSettings.GENE_STATS_FILE = options[1];
			} else if (options[0].equals("statsFile")) {
				OutputSettings.GENERAL_STATS_FILE = options[1];
			} else if (options[0].equals("-dataStatsFile")) {
				OutputSettings.DATASETS_STATS_FILE = options[1];
			} else if (options[0].equals("-resultsDir")) {
				params.RESULTS_FOLDER = options[1];
			} else if (options[0].equals("-spStatsFile")) {
				params.SHORTEST_PATHS_STATS_FILE = options[1];
			} else if (options[0].equals("-spFile")) {
				params.SHORTEST_PATH_FILE = options[1];
			} else if (options[0].equals("-spNodeStatsFile")) {
				params.SHORTEST_PATHS_NODE_STATS_FILE = options[1];
			} else if (options[0].equals("-spEdgeStatsFile")) {
				params.SHORTEST_PATHS_EDGE_STATS_FILE = options[1];            
			} else if (options[0].equals("-pSingleFile")) {
				params.PATHWAYS_IN_SINGLE_FILE = true;
			} else if (options[0].equals("-gSummary")) { //flags
				OutputSettings.GENERATE_SUMMARY_FILE =false;
			} else if (options[0].equals("-gPathways")) {
				OutputSettings.GENERATE_PATHWAYS_FILE = false;
			} else if (options[0].equals("-gPathwayStats")) {
				OutputSettings.GENERATE_PATHWAYS_STATS_FILE = false;
			} else if (options[0].equals("-gGeneStats")) {
				OutputSettings.GENERATE_GENE_STATS_FILE = false;
			} else if (options[0].equals("-gDataStats")) {
				OutputSettings.GENERATE_DATASETS_STATS_FILE = false;
			} else if (options[0].equals("-gSPStats")) {
				params.GENERATE_SHORTEST_PATHS_STATS_FILE = false;
			} else if (options[0].equals("-gSPFiles")) {
				params.GENERATE_SHORTEST_PATH_FILES = false;
			} else if (options[0].equals("-gSPNodes")) {
				params.GENERATE_SHORTEST_PATHS_NODE_STATS_FILE = false;
			} else if (options[0].equals("-gSPEdges")) {
				params.GENERATE_SHORTEST_PATHS_EDGE_STATS_FILE = false;
			} else if (options[0].equals("runID")) {
				params.RUN_ID = options[1];
				// OUTPUT TO TERMINAL
			} else if (options[0].equals("-pGraphStats")) {
				params.PRINT_GRAPH_STATS = false;
			} else if (options[0].equals("-pDataStats")) {
				params.PRINT_DATASETS_STATS = false;
			} else if (options[0].equals("-suffix")) {
				params.SUFFIX = options[1];
				// BASIC params
			} else if (options[0].equals("-program")) {
				params.PROGRAM = Program.valueOf(options[1]);
			} else if (options[0].equals("-sp")) {
				params.SHORTEST_PATHS_REPORTED = Integer.valueOf(options[1]);
			} else if (options[0].equals("-spPathways")) {
				params.PATHWAYS_SHORTEST_PATHS = Integer.valueOf(options[1]);
			} else if (options[0].equals("-spMinLength")) {
				params.MINIMUM_LENGTH_SHORTEST_PATHS = Integer.valueOf(options[1]);
			} else if (options[0].equals("-K")) {                
				kpmSettings.GENE_EXCEPTIONS = Integer.parseInt(options[1]);
				kpmSettings.MIN_K=kpmSettings.MAX_K=Integer.parseInt(options[1]);
			} else if (options[0].startsWith("-L") && !options[0].contains("batch") && !options[0].contains("pvalue")) {
				String id = "L" + options[0].substring(2);
				int l = Integer.parseInt(options[1]);

				String internalID = kpmSettings.externalToInternalIDManager.getOrCreateInternalIdentifier(id);
				kpmSettings.MIN_L.put(internalID, l);
				kpmSettings.INC_L.put(internalID, l);
				kpmSettings.MAX_L.put(internalID, l);
				id2param.put(internalID, l);
			} else if (options[0].equals("-algo")) {
				if (!algoList.contains(options[1])) {
					System.err.println(options[1]
							+ " is not a valid algorithm. " + ("Valid options are: GREEDY, ACO or OPTIMAL"));
					System.exit(-1);

				} else {
					algorithm = options[1];
				}

			} else if (options[0].equals("-strategy")) {
				if (!strategyList.contains(options[1])) {
					System.err.println(options[1]
							+ " is not a valid strategy. " + ("Valid options are: INES or GLONE"));
					System.exit(-1);

				} else {
					strategy = options[1];
				}

				// ADVANCED PARAMETERS (GENERAL)

			} else if (options[0].equals("-numProc")) {
				int input = Integer.parseInt(options[1]);
				int available = Runtime.getRuntime().availableProcessors();
				if (input < 1) {
					kpmSettings.NUMBER_OF_PROCESSORS = 1;
				} else if (input > available) {
					kpmSettings.NUMBER_OF_PROCESSORS = available;
				} else {
					kpmSettings.NUMBER_OF_PROCESSORS = input;
				}
			} else if (options[0].equals("-nodeHeuristic")) {
				kpmSettings.NODE_HEURISTIC_VALUE = Heuristic.valueOf(options[1]);
			} else if (options[0].equals("-combineOp")) {
				kpmSettings.COMBINE_OPERATOR = Combine.valueOf(options[1]);
			} else if (options[0].equals("-combineFormula")) {
				kpmSettings.COMBINE_FORMULA = options[1];
			} else if (options[0].equals("-eval")) {
				kpmSettings.EVAL = Boolean.parseBoolean(options[1]);
			} else if (options[0].equals("-maxsolutions")) {
				kpmSettings.NUM_SOLUTIONS = Integer.parseInt(options[1]);
			} else if (options[0].equals("-doubleSolutions")) {
				kpmSettings.DOUBLE_SOLUTIONS_ALLOWED = Boolean.parseBoolean(options[1]);

				// ADVANCED PARAMETERS (ACO ONLY)    
			} else if (options[0].equals("-alpha")) {
				kpmSettings.ALPHA = Double.parseDouble(options[1]);
			} else if (options[0].equals("-beta")) {
				kpmSettings.BETA = Double.parseDouble(options[1]);
			} else if (options[0].equals("-rho")) {
				kpmSettings.RHO = Double.parseDouble(options[1]);
			} else if (options[0].equals("-iterations")) {
				if (Integer.parseInt(options[1]) == 0) {
					kpmSettings.MAX_ITERATIONS = Integer.MAX_VALUE;
				} else {
					kpmSettings.MAX_ITERATIONS = Integer.parseInt(options[1]);
				}
			} else if (options[0].equals("-seed")) {
				kpmSettings.SEED = Long.parseLong(options[1]);
				kpmSettings.R = new Random(kpmSettings.SEED);
			} else if (options[0].equals("-tradeoff")
					&& options[1].equals("multiplicative")) {
				kpmSettings.MULTIPLICATIVE_TRADEOFF = true;
			} else if (options[0].equals("-tradeoff")
					&& options[1].equals("additive")) {
				kpmSettings.MULTIPLICATIVE_TRADEOFF = false;
			} else if (options[0].equals("-solutions")) {
				kpmSettings.NUMBER_OF_SOLUTIONS_PER_ITERATION = Integer.parseInt(options[1]);
			} else if (options[0].equals("-startnodes")) {
				kpmSettings.NUM_STARTNODES = Integer.parseInt(options[1]);
			} else if (options[0].equals("-rhodecay")) {
				try {
					kpmSettings.RHO_DECAY = RhoDecay.valueOf(options[1]);
				} catch (IllegalArgumentException e) {
					System.err.println(options[1]
							+ " is not a valid rho decay function. "
							+ Algo.values());
					System.exit(-1);
				}
			} else if (options[0].equals("-iterationbased")) {
				try {
					kpmSettings.ITERATION_BASED = Boolean.parseBoolean(options[1]);
				} catch (IllegalArgumentException e) {
					System.err.println(options[1]
							+ " is not a valid local search method. "
							+ LocalSearch.values());
					System.exit(-1);
				}
			} else if (options[0].equals("-localsearch")) {
				kpmSettings.L_SEARCH = LocalSearch.valueOf(options[1]);
			} else if (options[0].equals("-maxrunswithoutchange")) {
				if (Integer.parseInt(options[1]) == 0) {
					kpmSettings.MAX_RUNS_WITHOUT_CHANGE = Integer.MAX_VALUE;
				} else {
					kpmSettings.MAX_RUNS_WITHOUT_CHANGE = Integer.parseInt(options[1]);
				}
			} else if (options[0].equals("-tau_min")) {
				kpmSettings.TAU_MIN = Double.parseDouble(options[1]);
			} else if (options[0].equals("-batch")) {
				System.out.println("Should be batch run");
				kpmSettings.IS_BATCH_RUN = true;
            } else if (options[0].equals("-Umove_bens")) {
                kpmSettings.REMOVE_BENs = false;
			} else if (options[0].equals("-validation_file")) {
				params.VALIDATION_FILE = options[1];
			} else if (options[0].equals("-perturbation")) {
				params.IS_PERTURBATION_RUN = true;
				String[] values = options[1].split(",");

				if(values.length != 4){
					throw new Exception("Invalid settings for " + options[0]);
				}
				params.MIN_PERCENTAGE = Integer.parseInt(values[0]);
				params.STEP_PERCENTAGE = Integer.parseInt(values[1]);
				params.MAX_PERCENTAGE = Integer.parseInt(values[2]);
				params.GRAPHS_PER_STEP = Integer.parseInt(values[3]);
			}
			// Added setting for batch K
			else if (options[0].equals("-K_batch")) 
			{
				String[] values = options[1].split(",");

				if(values.length != 3){
					throw new Exception("Invalid settings for " + options[0]);
				}

				kpmSettings.MIN_K = Integer.parseInt(values[0]);
				kpmSettings.INC_K = Integer.parseInt(values[1]);
				kpmSettings.MAX_K = Integer.parseInt(values[2]);
			}
			// Added possibility for batch L1 or L2
			else if (options[0].matches("-L[1-9][0-9]*[_]+batch")) //matches 1 or more datasets.
			{
				String id = options[0].substring(1, options[0].indexOf('_'));
				String internalID = kpmSettings.externalToInternalIDManager.getOrCreateInternalIdentifier(id);
				String[] values = options[1].split(",");

				if(values.length != 3){
					throw new Exception("Invalid settings for " + options[0]);
				}

				kpmSettings.MIN_L.put(internalID, Integer.parseInt(values[0]));
				kpmSettings.INC_L.put(internalID, Integer.parseInt(values[1]));
				kpmSettings.MAX_L.put(internalID, Integer.parseInt(values[2]));
                kpmSettings.VARYING_L_ID.add(internalID);
			}
			// for which of the datasets should the L values interpreted as percentages
			else if(options[0].matches("-percentages")){
			    String[] datasets = options[1].split(",");
			    for (String id : datasets){
                    String internalID = kpmSettings.externalToInternalIDManager.getOrCreateInternalIdentifier(id);
			        kpmSettings.VARYING_L_ID_IN_PERCENTAGE.put(internalID, true);
                }

            }else if(options[0].matches("-L[1-9][0-9]*[_]+pvalues")){
                String id = options[0].substring(1, options[0].indexOf('_'));
                String internalID = kpmSettings.externalToInternalIDManager.getOrCreateInternalIdentifier(id);
                kpmSettings.PVALUE_FILES_MAP.put(internalID, options[1]);
            }

            else if(options[0].equals("-perturbation_technique")){
                if (options[1].equals("edgeremove")) {
                    params.PERTURBATION = PerturbationService.getPerturbation(PerturbationTags.EdgeRemoval);

                } else if (options[1].equals("edgerewire")) {
                    params.PERTURBATION = PerturbationService.getPerturbation(PerturbationTags.EdgeRewire);

                } else if (options[1].equals("nodeswap")) {
                    params.PERTURBATION = PerturbationService.getPerturbation(PerturbationTags.NodeSwap);

                } else if (options[1].equals("noderemove")) {
                    params.PERTURBATION = PerturbationService.getPerturbation(PerturbationTags.NodeRemoval);

                }
				
				if(params.PERTURBATION == null){
					throw new Exception("Invalid perturbation technique.");
				}
				
				System.out.println("Perturbation technique: " + params.PERTURBATION.getName());
			} else if(options[0].matches("-L[1-9][0-9]*[_]+pvaluecutoff")){
				String id = options[0].substring(1, options[0].indexOf('_'));
				String internalID = kpmSettings.externalToInternalIDManager.getOrCreateInternalIdentifier(id);
				kpmSettings.PVALUE_MAP.put(internalID, Double.parseDouble(options[1]));
				params.IS_BINARY_MATRIX = false;

            }else if(options[0].equals("-use_double")){
			    // use double values in matrix
			    kpmSettings.USE_DOUBLE_VALUES = true;
            }

            else if(options[0].equals("-comparator")){
			    kpmSettings.COMPARATOR = Comparator.valueOf(options[1]);
			    //comparator to decide how the matrix should be binarized
            }
			else if (options[0].equals("-significance_level")) {
				kpmSettings.SIGNIFICANCE_LEVEL = Double.parseDouble(options[1]);
			}
			else if (options[0].equals("-aggregation_method")) {
				kpmSettings.AGGREGATION_METHOD = options[1];
			}
			else if (options[0].equals("-randomized_graph_file")) {
				params.PERTURBED_GRAPH_FILE = options[1];
			}
			else if (options[0].equals("-help")) {
				printHelp();
				System.exit(0);
			} else {
				System.out.println("Unknown command line argument: "
						+ options[0]);
				System.out.println("Please type \"-help\" for a list of commands and usage.");
				System.exit(-1);
			}
		}


		if (params.PROGRAM == Program.SP) {
			// do nothing
		} else if (!id2path.isEmpty()) {
			for (String id : id2path.keySet()) {
				String file = id2path.get(id);
				System.out.println(id + ": " + file);
				if (!(new File(file)).isFile()) {
					System.out.println(file + " not found !");
					System.exit(-1);
				}
			}
			kpmSettings.MATRIX_FILES_MAP = id2path;

			if(kpmSettings.IS_BATCH_RUN){
				List<String> invalids = new ArrayList<String>();
				for(String id : id2param.keySet()){
					if(id.startsWith("L")){
						invalids.add(id);
					}
				}
				for(String invalidID : invalids){
					id2param.remove(invalidID);
				}
			}
			
			kpmSettings.CASE_EXCEPTIONS_MAP = id2param;
		} else if (params.DATASETS_FILE == null) {
			System.out.println("No datasets file was specified.");
			System.exit(-1);
		} else if (!new File(params.DATASETS_FILE).isFile()) {
			System.out.println("Datasets file " + params.DATASETS_FILE + " does not exist.");
			System.exit(-1);
		} else {
			DatasetsFileParser dfp = new DatasetsFileParser(kpmSettings);
			params = dfp.parse(params.DATASETS_FILE_SEPARATOR.charValue(), params.DATASETS_FILE_HAS_HEADER, params);
		}


		if(params.PROGRAM!=Program.SP) {
            if (kpmSettings.MIN_L.keySet().size() != kpmSettings.MATRIX_FILES_MAP.size()) {
                System.out.println(String.format(
                        "\nThe were found setup for %d L-parameters, this amount does not match the %d found for the matrix files map.\n\nKPM will now terminate.",
                        kpmSettings.MIN_L.keySet().size(),
                        kpmSettings.MATRIX_FILES_MAP.size()
                ));
                System.exit(-1);
            }
            // if not for all matrix files percentages should be used the VARYING_L_ID_IN_PERCENTAGE needs to be filled
            // with default values = false
            // VARYING_L_ID contains internal ids
            else if(kpmSettings.VARYING_L_ID.size() != kpmSettings.VARYING_L_ID_IN_PERCENTAGE.size()){
                for(String id: kpmSettings.VARYING_L_ID ){
                    if(!kpmSettings.VARYING_L_ID_IN_PERCENTAGE.containsKey(id)){
                        kpmSettings.VARYING_L_ID_IN_PERCENTAGE.put(id, false);
                    }
                }
            }
            // calculate number of exception nodes if percentages are used
            for(String internalId: kpmSettings.VARYING_L_ID_IN_PERCENTAGE.keySet()){
                if(kpmSettings.VARYING_L_ID_IN_PERCENTAGE.get(internalId)){
                    // put percentages
                    // get dataset size for each dataset
                    int dataSetSize = getDatasetSize(internalId, id2path);
                    // calculate min_L, inc_L, max_L
                    int new_min_L = (int) Math.ceil(((double) dataSetSize / (double) 100) * kpmSettings.MIN_L.get(internalId));
                    kpmSettings.MIN_L.put(internalId, new_min_L);
                    int new_inc_L = (int) Math.ceil(((double) dataSetSize / (double) 100) * kpmSettings.INC_L.get(internalId));
                    kpmSettings.INC_L.put(internalId, new_inc_L);
                    int new_max_L = (int) Math.ceil(((double) dataSetSize / (double) 100) * kpmSettings.MAX_L.get(internalId));
                    kpmSettings.MAX_L.put(internalId, new_max_L);

                }
                // else: leave the integer values, that have been put in the map anyway
            }
        }
        if( strategy.equals("FDR")){
		    kpmSettings.ALGO = Algo.FDR;
        }
		else if (strategy.equals("INES")) {
			if (algorithm.equals("GREEDY")) {
				kpmSettings.ALGO = Algo.GREEDY;
			} else if (algorithm.equals("ACO")) {
				kpmSettings.ALGO = Algo.LCG;
			} else if (algorithm.equals("OPTIMAL")) {
				kpmSettings.ALGO = Algo.OPTIMAL;
			} else {
				kpmSettings.ALGO = Algo.GREEDY;
			}
		} else if (strategy.equals("GLONE")) {
			if (algorithm.equals("GREEDY")) {
				kpmSettings.ALGO = Algo.EXCEPTIONSUMGREEDY;
			} else if (algorithm.equals("ACO")) {
				kpmSettings.ALGO = Algo.EXCEPTIONSUMACO;
			} else if (algorithm.equals("OPTIMAL")) {
				kpmSettings.ALGO = Algo.EXCEPTIONSUMOPTIMAL;
			} else {
				kpmSettings.ALGO = Algo.EXCEPTIONSUMGREEDY;
			}

		} else {
			if (algorithm.equals("GREEDY")) {
				kpmSettings.ALGO = Algo.GREEDY;
			} else if (algorithm.equals("ACO")) {
				kpmSettings.ALGO = Algo.LCG;
			} else if (algorithm.equals("OPTIMAL")) {
				kpmSettings.ALGO = Algo.OPTIMAL;
			} else {
				kpmSettings.ALGO = Algo.EXCEPTIONSUMGREEDY;
			}
		}

		System.out.println("Combine Formula : " + kpmSettings.COMBINE_FORMULA);
		return params;
	}

	private static void printHelp() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("README.txt"));
			String line = "";
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
			br.close();
		} catch (FileNotFoundException ex) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ioe) {
			Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ioe);
		} finally {
			try {
				br.close();
			} catch (IOException ex) {
				Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
			}
		}


	}
	private int getDatasetSize(String internalID, HashMap<String, String> id2path){
        int lines = 0;
	    try(BufferedReader br = new BufferedReader(new FileReader(id2path.get(internalID)))){
	        String [] line = br.readLine().split("\t");
	        // First row is gene name
	        lines = line.length-1;
        }
        catch (IOException ioe){
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ioe);
        }
        return lines;
    }
}
