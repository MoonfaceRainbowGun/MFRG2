//
//  ScanLineMapView.swift
//  SeaTalk
//
//  Created by Wang Jinghan on 3/8/18.
//  Copyright © 2018 Garena. All rights reserved.
//

import UIKit

class ScanLineMapView: UIView {
    var datasource: Datasource? {
        didSet{
            configureBlockViews()
        }
    }
    let backgroundImageView = UIImageView()
    var blockViews = [ScanLineMapBlockView]()
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        backgroundColor = UIColor(hex: "273646")
        backgroundImageView.image = #imageLiteral(resourceName: "mfrg-map")
        
        addSubview(backgroundImageView)
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        
        backgroundImageView.frame = bounds
        
        for blockView in blockViews {
            let block = blockView.blockModel.block
            blockView.frame = convertBlockFrame(block.frame, bounds: bounds)
        }
    }
}

extension ScanLineMapView {
    func configureBlockViews() {
        defer {
            setNeedsLayout()
        }
        
        for view in blockViews {
            view.removeFromSuperview()
        }
        
        blockViews = []
        
        guard let datasource = datasource else { return }
        
        for model in datasource.activeBlockModels() {
            let blockView = ScanLineMapBlockView(blockModel: model)
            blockView.frame = convertBlockFrame(model.block.frame, bounds: bounds)
            blockViews.append(blockView)
            addSubview(blockView)
        }
    }
    
    private func convertBlockFrame(_ frame: CGRect, bounds: CGRect) -> CGRect {
        let xOffset = bounds.width * frame.minX
        let yOffset = bounds.height * frame.minY
        let width = bounds.width * frame.width
        let height = bounds.width * frame.height
        return CGRect(
            x: bounds.minX + xOffset,
            y: bounds.minY + yOffset,
            width: width,
            height: height
        )
    }
}
