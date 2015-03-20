===========================================================================
CS 529 - Intro to Machine Learning - Assignment 2 - Naive Bayes Classifier
===========================================================================

1. Source code of the project is hosted on github:
	https://github.com/vamshins/CS529-ML-NaiveBayes

2. The executable NaiveBayes.jar (Java Archive) is provided in the assignment submission in UNM Learn

3. The program is compatible to run in any OS with Java 1.7 or higher installed. (I used Java 1.7)
   Download the jar from UNM Learn and store it in "<HOME_DIR>" (This may be any folder on your OS)
   Store all the data, label, txt files in the "<HOME_DIR>/data" folder.
   The folder structure looks like -
			- <HOME_DIR>
					|--> /NaiveBayes.jar
					|--> /data
						   |--> /newsgrouplabels.txt
						   |--> /test.data
						   |--> /test.label
						   |--> /train.data
						   |--> /train.label
						   |--> /vocabulary.txt

4. Execution of the Program:
	4.1. Go to Run(Windows) or Terminal(Linux)
	4.2. Navigate to <HOME_DIR>
	4.3. Run the command "java -jar DecisionTrees.jar [beta_value]"
		 The beta_value is optional. If no beta_value is given, the default value is taken i.e., 1/|V|
		 Note: This command took nearly 30 seconds for the execution of the program to complete.		 

5. Output of the Program with default beta_value (1/|V|)

		java -jar NaiveBayes.jar [beta_value]
		Options - 
			 beta_value (optional) - value varies from 0.00001 to 1
		Starting the application...
		-----------------------------------------------------------------------------------------------
		Beta value is not provided. Taking default value i.e., 1.0/vocabularyCount
			-> beta value : 1.6343073805321303E-5
		train.label count : 11269
		test.label count  :7505
		vocabularyCount count : 61188
		Getting the train.label contents in the matrix [DocId, Yk]...
			-> completed!
		Getting the test.label contents in the matrix [DocId, Yk]...
			-> completed!
		Calculating Yk counts...
			-> completed!
		Calculating Priors (pYk)...
			-> Calculation completed!
		Loading train.data file into the application in the matrix (docId, wordId, count)...
			-> Loaded train.data file!
		Loading test.data file into the application in the matrix (docId, wordId, count)...
			-> Loaded test.data file!
		Calculating and loading Count of all the WordIds in different Categories... (count of Xi in Yk)
			-> Xi - WordId
			-> Yk - Category/Class
			-> Calculation and loading completed!
		Calculating MAPs... (MAP for P(X|Y))
			-> using P(Xi|Yk)=(count of Xi in Yk)+(beta)(total words in Yk)+((beta)*(length of vocab list)))
			-> Calculation completed
		Calculating the classifications of all the documents...
			-> using Ynew=argmax[ log2(P(Yk))+(sum over i)(# of Xnewi)log2(P(Xi|Yk))]
				-> Iterations left : 7500
				-> Iterations left : 7000
				-> Iterations left : 6500
				-> Iterations left : 6000
				-> Iterations left : 5500
				-> Iterations left : 5000
				-> Iterations left : 4500
				-> Iterations left : 4000
				-> Iterations left : 3500
				-> Iterations left : 3000
				-> Iterations left : 2500
				-> Iterations left : 2000
				-> Iterations left : 1500
				-> Iterations left : 1000
				-> Iterations left : 500
				-> Iterations left : 0
			-> Calculation completed
		Creating empty Confusion Matrix...
			-> created
		Calculating correctly classified labels and populating Confusion Matrix
			-> completed

		Accuracy of classification of test labels: 69.51365756162559 %

		Printing Confusion Matrix : 

		226	0	1	0	2	0	0	0	0	0	0	0	4	5	0	5	0	2	2	32	
		0	215	22	11	19	19	18	1	1	1	0	2	8	4	5	1	0	0	0	0	
		0	4	123	19	2	0	4	1	0	0	0	0	0	0	0	0	0	0	0	0	
		0	7	41	246	24	3	47	0	1	1	0	1	8	1	1	0	0	0	0	0	
		0	2	6	11	188	0	14	0	0	0	0	0	1	0	0	0	0	0	0	0	
		1	62	82	9	15	319	7	0	0	1	0	1	5	0	4	0	1	0	1	0	
		0	0	0	3	2	0	136	2	0	0	0	0	2	0	0	0	1	0	0	0	
		0	0	1	0	2	0	14	301	22	0	0	0	5	0	0	0	0	0	0	0	
		0	0	0	1	1	0	4	6	309	1	0	0	3	0	0	0	0	0	0	0	
		0	0	0	0	0	0	2	0	0	282	1	0	0	0	0	0	0	1	0	0	
		0	2	0	1	0	0	3	0	0	23	373	0	0	0	1	0	1	0	0	0	
		2	45	55	34	49	28	18	6	5	4	1	364	85	4	2	0	7	1	6	3	
		0	3	2	28	8	0	15	3	1	0	0	0	202	3	3	0	0	0	0	0	
		2	11	14	3	13	5	13	3	2	1	0	1	22	288	4	2	3	0	3	1	
		3	10	8	7	6	3	16	5	1	3	0	2	17	5	319	0	1	0	4	5	
		31	3	3	3	1	0	4	3	0	6	0	0	3	14	1	354	1	2	2	33	
		0	1	5	0	7	1	9	9	7	5	3	7	7	6	4	0	258	1	37	9	
		18	7	6	6	13	6	23	13	19	13	10	5	13	24	15	12	22	354	29	11	
		11	17	21	9	31	6	33	42	28	56	11	12	8	38	33	14	58	15	221	18	
		24	0	1	1	0	0	2	0	1	0	0	0	0	1	0	10	11	0	5	139	
		
		Loading vocabulary.txt into the application...
			-> completed

		Printing top 100 words with highest measure...
		the of to and in that is it you they was for not be are on this have we as were by god from with but he there space or key their if can will all people who at what windows an no israel gun would one about edu had do my car them has writes window so me your his encryption turkish com article armenian when more scsi drive team out don jews israeli said think armenians file jesus president game nasa mr hockey chip been just image some government db which any use graphics clipper bike up only 

		Execution completed! Exiting the application.

6. If you want to generate the Accuracies and Confusion Matrix for different beta values, use "NB_betas.bat".
   Edit the file to input different beta values.
   Execution command : NB_betas.bat > log.txt
   This command stores the output of the program in the log.txt file. Please open the log.txt file, check the accuracies and confusion matrices for different beta values. This file is provided as part of the submission.
   Note: This command took nearly 10 minutes (considering 20 beta values) for the execution of the program to complete.