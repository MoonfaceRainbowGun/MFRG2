//
//  ScanLineNetworkManager.swift
//  SeaTalk
//
//  Created by Lei Mingyu on 3/8/18.
//  Copyright © 2018 Garena. All rights reserved.
//

import Foundation

class ScanLineNetworkManager: NSObject {
    private static let BASE_SERVER_ADDRESS = "http://203.116.214.78:2546/"
    private static let HEAT_MAP_ENDPOINT = "getheatmap/"
    private static let PEOPLE_ENDPOINT = "getpeople/"
    
    static private func makePostRequest(url: String, data: [String: Any], handler: @escaping (Data) -> Void) {
        guard let destination = URL(string: url) else {
            return
        }
        var request = URLRequest(url: destination)
        var requestBody: Data
        request.httpMethod = "POST"
        do {
            requestBody = try JSONSerialization.data(withJSONObject: data, options: [])
            request.httpBody = requestBody
        } catch {
            return
        }
        
        let session = URLSession.shared
        let task = session.dataTask(with: request) { (data, response, error) in
            // I'm so confident that there is no error since this is written by Cao Shen
            guard let responseData = data else {
                return
            }
            handler(responseData)
        }
        
        task.resume()
    }
    
    static public func getHeatMap(start: UInt64, end: UInt64, handler: @escaping ([ScanLineMapView.Datasource.ActiveMeasurement]) -> Void) {
        let url = BASE_SERVER_ADDRESS + HEAT_MAP_ENDPOINT
        let data = [
            "start": start,
            "end": end
        ]
        makePostRequest(url: url, data: data) { (data) in
            let response = try! JSONDecoder().decode(HeatMapResponse.self, from: data)
            let blocks = response.blks.map({ (model) -> ScanLineMapView.Datasource.ActiveMeasurement in
                // We trust Cao Shen again
                guard let targetBlock = Galaxis16F.blocks.filter({ (block) -> Bool in
                    return block.index == model.id
                }).first else {
                    fatalError("It's impossible")
                }
                let crowdness = ScanLineMapView.Datasource.calcCrowdness(population: model.number, capacity: targetBlock.capacity)
                return .init(blockIndex: model.id, crowdness: crowdness)
            })
            handler(blocks)
        }
    }
    
    static public func getPerson(uid: String, handler: @escaping (ScanLineMapView.Datasource.Block) -> Void) {
        let url = BASE_SERVER_ADDRESS + PEOPLE_ENDPOINT
        let data: [String: Any] = [
            "start": 0,
            "end": 99999999999,
            "uids": [uid]
        ]
        makePostRequest(url: url, data: data) { (data) in
            let response = try! JSONDecoder().decode(PersonResponse.self, from: data)
            guard let blockId = response.people.first?.block else {
                fatalError("It's impossible too")
            }
            guard let block = Galaxis16F.blocks.filter({ (block) -> Bool in
                return block.index == blockId
            }).first else {
                fatalError("Again, it's impossible too")
            }
            handler(block)
        }
    }
}

extension ScanLineNetworkManager {
    struct HeatMapModel: Codable {
        let id: String
        let number: Int
    }
    
    struct HeatMapResponse: Codable {
        let blks: [HeatMapModel]
    }
    
    struct PersonModel: Codable {
        let uid: String
        let block: String
        let timestamp: Int
    }
    
    struct PersonResponse: Codable {
        let people: [PersonModel]
    }
}
