//
//  ScanLineMapViewDatasource.swift
//  SeaTalk
//
//  Created by Wang Jinghan on 3/8/18.
//  Copyright © 2018 Garena. All rights reserved.
//

import UIKit

extension ScanLineMapView {
    class Datasource {
        let activeBlockMeasurements: [ActiveMeasurement]
        
        init(activeBlockMeasurements: [ActiveMeasurement]) {
            self.activeBlockMeasurements = activeBlockMeasurements
        }
    }
}

extension ScanLineMapView.Datasource {
    func activeBlockModels() -> [BlockUIModel] {
        return Galaxis16F.blocks.compactMap({ (block) -> BlockUIModel? in
            if let measurement = self.activeBlockMeasurements.filter({ $0.blockIndex == block.index }).first {
                return BlockUIModel(block: block, crowdness: measurement.crowdness)
            } else {
                return nil
            }
        })
    }
    
    static func calcCrowdness(population: Int, capacity: Int) -> Crowdness {
        let ratio = Float(population) / Float(capacity)
        var crowdness: Crowdness = .NearlyEmpty
        if ratio > 0 {
            crowdness = .StillAvailable
        }
        if ratio > 0.6 {
            crowdness = .FullyPacked
        }
        if ratio > 1 {
            crowdness = .AlreadyFuckedUp
        }
        
        return crowdness
    }
}

extension ScanLineMapView.Datasource {
    enum Crowdness {
        case NearlyEmpty
        case StillAvailable
        case FullyPacked
        case AlreadyFuckedUp
    }
    
    struct ActiveMeasurement: Hashable {
        var blockIndex: String
        var crowdness: Crowdness
    }
    
    struct BlockUIModel {
        var block: Block
        var crowdness: Crowdness
    }
    
    struct Block {
        let frame: CGRect
        let name: String
        let index: String
        let capacity: Int
    }
}
