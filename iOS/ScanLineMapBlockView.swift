//
//  ScanLineMapBlockView.swift
//  SeaTalk
//
//  Created by Wang Jinghan on 2/8/18.
//  Copyright © 2018 Garena. All rights reserved.
//

import UIKit

class ScanLineMapBlockView: UIView {
    typealias BlockUIModel = ScanLineMapView.Datasource.BlockUIModel
    let blockModel: BlockUIModel
    init(blockModel: ScanLineMapView.Datasource.BlockUIModel) {
        self.blockModel = blockModel
        super.init(frame: .zero)
        
        configureViews()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}

extension ScanLineMapBlockView {
    func configureViews() {
        switch blockModel.crowdness {
        case .StillAvailable:
            layer.backgroundColor = UIColor(red: 46.0 / 255.0, green: 224.0 / 255.0, blue: 213.0 / 255.0, alpha: 1).cgColor
        case .FullyPacked:
            layer.backgroundColor = UIColor(red: 241.0 / 255.0, green: 196.0 / 255.0, blue: 16.0 / 255.0, alpha: 1).cgColor
        case .AlreadyFuckedUp:
            layer.backgroundColor = UIColor(red: 230.0 / 255.0, green: 76.0 / 255.0, blue: 60.0 / 255.0, alpha: 1).cgColor
        default:
            layer.backgroundColor = UIColor.clear.cgColor
        }
    }
}
