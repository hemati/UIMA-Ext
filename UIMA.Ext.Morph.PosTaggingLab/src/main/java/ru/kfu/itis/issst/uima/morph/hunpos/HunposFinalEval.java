package ru.kfu.itis.issst.uima.morph.hunpos;

import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.google.common.collect.Sets;

import de.tudarmstadt.ukp.dkpro.lab.uima.task.UimaTask;
import ru.kfu.itis.cll.uima.util.CorpusUtils.PartitionType;
import ru.ksu.niimm.cll.uima.morph.lab.FinalEvalLauncherBase;

public class HunposFinalEval extends FinalEvalLauncherBase {

	public static void main(String[] args) throws Exception {
		System.setProperty("DKPRO_HOME", HunposLab.DEFAULT_WRK_DIR);
		HunposFinalEval lab = new HunposFinalEval();
		new JCommander(lab, args);
		lab.run();
	}

	@Parameter(names = { "-p", "--pos-categories" }, required = true)
	private List<String> posCategoriesList;

	private HunposFinalEval() {
	}

	private void run() throws Exception {
		posCategories = Sets.newHashSet(posCategoriesList);
		//
		UimaTask analysisTask = new HunposLab.AnalysisTask(PartitionType.TEST, inputTS,
				morphDictDesc);
		run(analysisTask);
	}
}
