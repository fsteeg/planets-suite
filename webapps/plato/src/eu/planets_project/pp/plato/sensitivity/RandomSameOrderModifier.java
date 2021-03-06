/*******************************************************************************
 * Copyright (c) 2006-2010 Vienna University of Technology, 
 * Department of Software Technology and Interactive Systems
 *
 * All rights reserved. This program and the accompanying
 * materials are made available under the terms of the
 * Apache License, Version 2.0 which accompanies
 * this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0 
 *******************************************************************************/
package eu.planets_project.pp.plato.sensitivity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import eu.planets_project.pp.plato.model.tree.TreeNode;

/**
 * This weight modifier assigns random weights to children while preserving importance ordering.
 * 
 * This meens that if you have three children with the following weights: A=0.3, B=0.6, C=0.4 
 * then B will remail the most important while A will be the least important. However no guarantee
 * is given about what the actual resulting weights will be, only that their ordering remains the same.
 *  
 * @author Jan Zarnikov
 *
 */
public class RandomSameOrderModifier implements IWeightModifier {
    
    private static final int ITERATIONS = 300;
    
    private Map<Double, List<TreeNode>> sortedChildren = null;
    
    private List<Double> sortedKeys = null;
    
    private int counter = 0;

    public boolean performModification(TreeNode node) {
        
        // Fortunately we have to do it only once for each node.
        if(sortedChildren == null) {
            
            // We build up a map that contains all the children:
            // The weight is the key and the children with this weight are the values
            
            // Example: A=0.1, B=0.3, C=0.5, D=0.1 will produce:
            // 0.1 -> {A, D}; 0.3 -> {B}; 0.5 -> {C}
            
            sortedChildren = new HashMap<Double, List<TreeNode>>();
            
            for(TreeNode child : node.getChildren()) {
                double weight = child.getWeight();
                if(sortedChildren.get(weight) == null) {
                    List<TreeNode> l = new LinkedList<TreeNode>();
                    l.add(child);
                    sortedChildren.put(weight, l);
                } else {
                    sortedChildren.get(weight).add(child);
                }
            }
            
            sortedKeys = new LinkedList<Double>(sortedChildren.keySet());
            Collections.sort(sortedKeys);
            
        }
        
        // now we have to generate the new random weights
        List<Double> randomWeights = new LinkedList<Double>();
        for(int i = 0; i < sortedChildren.size(); i++) {
            randomWeights.add(Math.random());
        }
        Collections.sort(randomWeights);
        
        Iterator<Double> randomWeightsIterator = randomWeights.iterator();
        
        
        // now iterate over the keys of the sortedChildren map we have generated at the beginning.
        // at the same time we iterate over random weights and assign them to the children.
        for(Double w : sortedKeys) {
            double newWeight = randomWeightsIterator.next();
            for(TreeNode tn : sortedChildren.get(w)) {
                tn.setWeight(newWeight);
            }
        }

        counter++;
        
        if(counter < ITERATIONS) {
            return true;
        } else {
            sortedChildren = null;
            return false;
        }
    }
    
}
