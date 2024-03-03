/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.carbonea.modello;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author anton
 */
public class Modello {
    
    private Map<String, Object> mappaBeans = new HashMap<>();
    
    public void putBean(String key, Object bean){
        this.mappaBeans.put(key, bean);
    }
    
    public Object getBean(String key){
        return this.mappaBeans.get(key);
    }
    
}
